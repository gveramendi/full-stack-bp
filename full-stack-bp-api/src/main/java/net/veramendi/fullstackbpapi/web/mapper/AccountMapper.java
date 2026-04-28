package net.veramendi.fullstackbpapi.web.mapper;

import net.veramendi.fullstackbpapi.domain.Account;
import net.veramendi.fullstackbpapi.domain.Client;
import net.veramendi.fullstackbpapi.domain.enums.Status;
import net.veramendi.fullstackbpapi.web.dto.account.AccountCreateRequest;
import net.veramendi.fullstackbpapi.web.dto.account.AccountResponse;
import net.veramendi.fullstackbpapi.web.dto.account.AccountUpdateRequest;

public final class AccountMapper {

    private AccountMapper() {}

    public static Account toEntity(AccountCreateRequest req, Client client) {
        Account account = new Account();
        account.setAccountNumber(req.accountNumber());
        account.setAccountType(req.accountType());
        account.setInitialBalance(req.initialBalance());
        account.setCurrentBalance(req.initialBalance());
        account.setStatus(req.status() != null ? req.status() : Status.ACTIVE);
        account.setClient(client);

        return account;
    }

    public static void applyUpdate(Account account, AccountUpdateRequest req) {
        if (req.accountNumber() != null) 
            account.setAccountNumber(req.accountNumber());
        
        if (req.accountType() != null) 
            account.setAccountType(req.accountType());
        
        if (req.status() != null) 
            account.setStatus(req.status());
    }

    public static AccountResponse toResponse(Account account) {
        Client client = account.getClient();
        
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getInitialBalance(),
                account.getCurrentBalance(),
                account.getStatus(),
                client.getClientId(),
                client.getName(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
