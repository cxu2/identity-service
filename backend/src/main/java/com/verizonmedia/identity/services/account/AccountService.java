package com.verizonmedia.identity.services.account;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface AccountService {

    @Nonnull
    AccountCreate newAccountCreate();

    @Nonnull
    Account getAccount(@Nonnull String id);

    @Nonnull
    Account getPublicAccount(@Nonnull String id);

    @Nonnull
    AccountUpdate newAccountUpdate(@Nonnull String id);

    void verifyAccountPassword(@Nonnull String username, @Nonnull String password);
}
