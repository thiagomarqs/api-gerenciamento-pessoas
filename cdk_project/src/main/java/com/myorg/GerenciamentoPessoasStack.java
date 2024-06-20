package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ClusterProps;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateServiceProps;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

import java.util.Collections;
import java.util.Map;

public class GerenciamentoPessoasStack extends Stack {

    public GerenciamentoPessoasStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public GerenciamentoPessoasStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        CfnParameter databasePass = CfnParameter.Builder.create(this, "databasePass")
                .type("String")
                .description("The database password")
                .build();

        CfnParameter databaseAdminUserName = CfnParameter.Builder.create(this, "databaseAdminUserName")
                .type("String")
                .description("The name of the database admin user")
                .build();

        CfnParameter springProfilesActive = CfnParameter.Builder.create(this, "springProfilesActive")
                .type("String")
                .description("The active profile of the application")
                .build();

        Vpc vpc = new Vpc(this, "VPC", VpcProps.builder()
                .maxAzs(2)
                .natGateways(0)
                .build());

        ISecurityGroup securityGroup = SecurityGroup.fromSecurityGroupId(this, id, vpc.getVpcDefaultSecurityGroup());
        securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(3306));

        DatabaseInstance rds = DatabaseInstance.Builder.create(this, "Rds")
                .instanceIdentifier("gerenciamento-pessoas")
                .vpc(vpc)
                .availabilityZone(vpc.getAvailabilityZones().get(0))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.MICRO))
                .engine(
                        DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder()
                                .version(MysqlEngineVersion.VER_8_0)
                                .build())
                )
                .allocatedStorage(10)
                .backupRetention(Duration.days(0))
                .databaseName("people_manager")
                .credentials(
                        Credentials.fromUsername(
                                databaseAdminUserName.getValueAsString(),
                                CredentialsFromUsernameOptions.builder()
                                        .password(SecretValue.unsafePlainText(databasePass.getValueAsString()))
                                        .build())
                )
                .securityGroups(Collections.singletonList(securityGroup))
                .vpcSubnets(
                        SubnetSelection.builder()
                                .subnets(vpc.getPublicSubnets())
                                .build()
                )
                .build();

        Cluster cluster = new Cluster(this, "GerenciamentoPessoasCluster", ClusterProps.builder()
                .clusterName("gerenciamento-pessoas-cluster")
                .vpc(vpc)
                .build());

        Map<String, String> containerEnvironmentVariables = Map.of(
                "SPRING_DATASOURCE_URL", "jdbc:mysql://" + rds.getDbInstanceEndpointAddress() + ":3306/people_manager?createDatabaseIfNotExist=true&useSSL=false",
                "SPRING_DATASOURCE_USERNAME", databaseAdminUserName.getValueAsString(),
                "SPRING_DATASOURCE_PASSWORD", databasePass.getValueAsString(),
                "SPRING_PROFILES_ACTIVE", springProfilesActive.getValueAsString()
        );

        ApplicationLoadBalancedFargateService service = new ApplicationLoadBalancedFargateService(this, "GerenciamentoPessoasService", ApplicationLoadBalancedFargateServiceProps.builder()
                .serviceName("gerenciamento-pessoas")
                .cluster(cluster)
                .cpu(512)
                .memoryLimitMiB(1024)
                .desiredCount(2)
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                            .containerName("GerenciamentoPessoas")
                            .image(ContainerImage.fromRegistry("thiagomarqs/gerenciamentopessoas:1.0.3"))
                            .containerPort(8080)
                            .environment(containerEnvironmentVariables)
                            .build())
                .publicLoadBalancer(true)
                .build());

        service.getTargetGroup()
                .configureHealthCheck(new HealthCheck.Builder()
                .path("/actuator/health")
                .port("8080")
                .healthyHttpCodes("200")
                .build());

    }
}
