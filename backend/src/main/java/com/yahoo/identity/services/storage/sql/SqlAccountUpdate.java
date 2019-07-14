package com.yahoo.identity.services.storage.sql;

import static com.kosprov.jargon2.api.Jargon2.jargon2Hasher;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

import com.kosprov.jargon2.api.Jargon2;
import com.yahoo.identity.IdentityException;
import com.yahoo.identity.services.account.AccountUpdate;
import com.yahoo.identity.services.random.RandomService;
import com.yahoo.identity.services.storage.AccountModel;
import com.yahoo.identity.services.system.SystemService;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

public class SqlAccountUpdate implements AccountUpdate {

    private final RandomService randomService;
    private final SystemService systemService;
    private final AccountModel account;
    private final SqlSessionFactory sqlSessionFactory;


    public SqlAccountUpdate(@Nonnull SqlSessionFactory sqlSessionFactory,
                            @Nonnull RandomService randomService,
                            @Nonnull SystemService systemService,
                            @Nonnull String username) {
        this.account = new AccountModel();
        this.sqlSessionFactory = sqlSessionFactory;
        this.randomService = randomService;
        this.systemService = systemService;
        this.account.setUsername(username);
    }

    @Override
    @Nonnull
    public AccountUpdate setEmail(@Nonnull String email) {
        account.setEmail(escapeHtml4(email));
        return this;
    }

    @Override
    @Nonnull
    public AccountUpdate setEmailStatus(boolean emailStatus) {
        account.setEmailVerified(emailStatus);
        return this;
    }

    @Override
    @Nonnull
    public AccountUpdate setPassword(@Nonnull String password) {

        byte[] saltBytes = new byte[64];
        this.randomService.getRandomBytes(saltBytes);

        Jargon2.Hasher hasher = jargon2Hasher();
        account.setPasswordHash(hasher.salt(saltBytes).password(password.getBytes(StandardCharsets.UTF_8)).encodedHash());
        return this;
    }

    @Nonnull
    @Override
    public AccountUpdate setDescription(@Nonnull String title) {
        account.setDescription(title);
        return this;
    }

    @Nonnull
    @Override
    public String update() throws IdentityException {
        account.setUpdateTs(systemService.currentTimeMillis());
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            mapper.updateAccount(account);
            session.commit();
        }
        return account.getUsername();
    }
}