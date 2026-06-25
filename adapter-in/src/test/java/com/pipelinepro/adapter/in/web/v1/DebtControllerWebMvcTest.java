package com.pipelinepro.adapter.in.web.v1;

import com.pipelinepro.adapter.in.web.error.GlobalRestExceptionHandler;
import com.pipelinepro.adapter.in.web.mapper.DebtWebMapper;
import com.pipelinepro.adapter.in.web.v1.dto.response.DebtListResponse;
import com.pipelinepro.adapter.in.web.v1.dto.response.DebtResponse;
import com.pipelinepro.domain.Debt;
import com.pipelinepro.domain.DebtStatus;
import com.pipelinepro.domain.port.in.QueryDebtUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DebtController.class)
@Import(GlobalRestExceptionHandler.class)
class DebtControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QueryDebtUseCase queryDebtUseCase;

    @MockitoBean
    private DebtWebMapper debtWebMapper;

    @Test
    void listDebtorDebts_shouldReturn200_whenDebtsExist() throws Exception {
        UUID debtorId = UUID.randomUUID();
        Debt debt = Debt.open(UUID.randomUUID(), debtorId, "D-001", new BigDecimal("100.00"), "EUR", null, Instant.parse("2026-06-01T10:00:00Z"));
        DebtListResponse response = new DebtListResponse(
                debtorId,
                List.of(new DebtResponse(
                        debt.id(),
                        debtorId,
                        new BigDecimal("100.00"),
                        "EUR",
                        DebtStatus.OPEN,
                        null,
                        0L,
                        Instant.parse("2026-06-01T10:00:00Z"),
                        Instant.parse("2026-06-01T10:00:00Z"))));

        when(queryDebtUseCase.listDebtsByDebtor(any(UUID.class), any())).thenReturn(List.of(debt));
        when(debtWebMapper.toDebtListResponse(any(UUID.class), any())).thenReturn(response);

        mockMvc.perform(get("/debtors/{debtorId}/debts", debtorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.debtorId").value(debtorId.toString()));
    }

    @Test
    void getDebt_shouldReturn404ProblemDetail_whenDebtIsMissing() throws Exception {
        UUID debtId = UUID.randomUUID();
        when(queryDebtUseCase.getDebt(debtId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/debts/{debtId}", debtId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.path").value("/debts/" + debtId))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
