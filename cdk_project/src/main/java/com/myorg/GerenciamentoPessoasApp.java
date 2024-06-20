package com.myorg;

import software.amazon.awscdk.App;

public class GerenciamentoPessoasApp {

    public static void main(final String[] args) {
        App app = new App();

        GerenciamentoPessoasStack gerenciamentoPessoasStack = new GerenciamentoPessoasStack(app, "GerenciamentoPessoasAppStack");

        app.synth();
    }
}

