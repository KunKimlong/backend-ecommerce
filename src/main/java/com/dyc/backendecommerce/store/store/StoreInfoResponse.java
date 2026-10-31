package com.dyc.backendecommerce.store.store;

public record StoreInfoResponse(
    String name,
    String description,
    String logoPath,
    String darkLogoPath,
    String faviconPath,
    String location,
    String supportEmail,
    String supportPhoneNumber) {}
