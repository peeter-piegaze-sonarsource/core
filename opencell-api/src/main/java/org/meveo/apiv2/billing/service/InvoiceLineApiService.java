package org.meveo.apiv2.billing.service;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.InvoiceLine;

import java.util.List;
import java.util.Optional;

public class InvoiceLineApiService implements ApiService<InvoiceLine> {
    @Override
    public List<InvoiceLine> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<InvoiceLine> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public InvoiceLine create(InvoiceLine baseEntity) {
        return null;
    }

    @Override
    public Optional<InvoiceLine> update(Long id, InvoiceLine baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceLine> patch(Long id, InvoiceLine baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceLine> delete(Long id) {
        return Optional.empty();
    }
}
