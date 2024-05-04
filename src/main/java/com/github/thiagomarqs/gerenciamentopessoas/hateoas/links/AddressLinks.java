package com.github.thiagomarqs.gerenciamentopessoas.hateoas.links;

import com.github.thiagomarqs.gerenciamentopessoas.controller.AddressController;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class AddressLinks {

    public static Link getPersonAddresses(Long personId) {
        return linkTo(methodOn(AddressController.class).getPersonAddresses(null, personId)).withRel("addresses");
    }

    public static Link getAddress(Long personId, Long addressId) {
        return linkTo(methodOn(AddressController.class).getAddress(personId, addressId)).withSelfRel();
    }

    public static Link getAllByAddressLike(Long personId) {
        return linkTo(methodOn(AddressController.class).getAllByAddressLike(personId, null)).withRel("searchAddresses");
    }

    public static Link getMainAddress(Long personId) {
        return linkTo(methodOn(AddressController.class).getMainAddress(personId)).withRel("mainAddress");
    }

    public static Link[] getIndividualAddressLinks(Long personId, Long addressId) {
        return new Link[] {
                PersonLinks.getOne(personId, "person"),
                getAddress(personId, addressId),
                getPersonAddresses(personId),
                getAllByAddressLike(personId)
        };
    }

    public static Link[] getAddressCollectionLinks(Long personId) {
        return new Link[] {
                PersonLinks.getOne(personId, "person"),
                getPersonAddresses(personId),
                getAllByAddressLike(personId)
        };
    }

}
