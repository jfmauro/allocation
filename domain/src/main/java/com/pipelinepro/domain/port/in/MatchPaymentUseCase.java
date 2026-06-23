package com.pipelinepro.domain.port.in;

import com.pipelinepro.domain.AllocationProposal;
import com.pipelinepro.domain.port.in.command.MatchPaymentCommand;

public interface MatchPaymentUseCase {
    AllocationProposal matchPayment(MatchPaymentCommand command);
}
