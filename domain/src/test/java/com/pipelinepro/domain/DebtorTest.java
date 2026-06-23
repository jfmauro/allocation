package com.pipelinepro.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DebtorTest {

    @Test
    void should_create_active_natural_person_debtor() {
        Debtor debtor = Debtor.activeNaturalPerson(
                UUID.randomUUID(),
                "Jane Doe",
                "hash-123",
                "enc-123",
                Instant.now());

        assertThat(debtor.type()).isEqualTo(DebtorType.NATURAL_PERSON);
        assertThat(debtor.nationalNumberHash()).contains("hash-123");
        assertThat(debtor.enterpriseNumber()).isEmpty();
        assertThat(debtor.active()).isTrue();
    }

    @Test
    void should_reject_inconsistent_identifiers_for_enterprise() {
        assertThatThrownBy(() -> new Debtor(
                UUID.randomUUID(),
                DebtorType.ENTERPRISE,
                "Acme SA",
                "niss-hash",
                null,
                "0820501224",
                true,
                Instant.now()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Enterprise debtor cannot define national number fields");
    }

    @Test
    void should_reject_natural_person_without_identifiers() {
        assertThatThrownBy(() -> new Debtor(
                UUID.randomUUID(),
                DebtorType.NATURAL_PERSON,
                "Jane Doe",
                null,
                null,
                null,
                true,
                Instant.now()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires national number identifiers");
    }

    @Test
    void should_deactivate_debtor() {
        Debtor debtor = Debtor.activeEnterprise(UUID.randomUUID(), "Acme SA", "0820501224", Instant.now());

        debtor.deactivate();
        assertThat(debtor.active()).isFalse();
    }
}
