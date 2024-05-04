package com.github.thiagomarqs.gerenciamentopessoas.hateoas.links;

import com.github.thiagomarqs.gerenciamentopessoas.controller.PersonController;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class PersonLinks {

    public static Link getOne(Long id) {
        return getOne(id, null);
    }

    public static Link getOne(Long id, String rel) {
        return linkTo(methodOn(PersonController.class).getOne(id)).withRel(rel == null ? "self" : rel);
    }

    public static Link getAllByFullNameLike() {
        return linkTo(methodOn(PersonController.class).getAllByFullNameLike(null)).withRel("searchByFullName");
    }

    public static Link getAllByCity() {
        return linkTo(methodOn(PersonController.class).getAllByCity(null)).withRel("searchByCity");
    }

    public static Link getAll() {
        return linkTo(methodOn(PersonController.class).getAll(null, null)).withRel("allPeople");
    }

    public static Link[] individualPersonLinks(Long personId) {
        return new Link [] {
                getOne(personId),
                getAll(),
                getAllByCity(),
                getAllByFullNameLike(),
                AddressLinks.getPersonAddresses(personId),
                AddressLinks.getMainAddress(personId),
                AddressLinks.getAllByAddressLike(personId)
        };
    }

    public static Link[] personCollectionLinks() {
        return new Link[] {
                getAll(),
                getAllByCity(),
                getAllByFullNameLike()
        };
    }

}
