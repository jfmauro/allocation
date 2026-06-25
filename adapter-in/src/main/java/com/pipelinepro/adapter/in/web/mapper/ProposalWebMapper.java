package com.pipelinepro.adapter.in.web.mapper;

import com.pipelinepro.adapter.in.web.v1.dto.request.MarkUnmatchedRequest;
import com.pipelinepro.adapter.in.web.v1.dto.request.RejectProposalRequest;
import com.pipelinepro.adapter.in.web.v1.dto.request.RequestInvestigationRequest;
import com.pipelinepro.adapter.in.web.v1.dto.request.SelectDebtRequest;
import com.pipelinepro.adapter.in.web.v1.dto.request.ValidateProposalRequest;
import com.pipelinepro.adapter.in.web.v1.dto.response.AllocationProposalListResponse;
import com.pipelinepro.adapter.in.web.v1.dto.response.AllocationProposalCandidateResponse;
import com.pipelinepro.adapter.in.web.v1.dto.response.AllocationProposalResponse;
import com.pipelinepro.adapter.in.web.v1.dto.response.AllocationProposalSummaryResponse;
import com.pipelinepro.adapter.in.web.v1.dto.response.AllocationResultResponse;
import com.pipelinepro.adapter.in.web.v1.dto.response.ProposalStateResponse;
import com.pipelinepro.domain.AllocationProposal;
import com.pipelinepro.domain.AllocationProposalCandidate;
import com.pipelinepro.domain.PaymentAllocation;
import com.pipelinepro.domain.port.in.command.MarkUnmatchedCommand;
import com.pipelinepro.domain.port.in.command.RejectProposalCommand;
import com.pipelinepro.domain.port.in.command.RequestInvestigationCommand;
import com.pipelinepro.domain.port.in.command.SelectDebtCommand;
import com.pipelinepro.domain.port.in.command.ValidateProposalCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProposalWebMapper {

    @Mapping(target = "proposalId", source = "proposalId")
    @Mapping(target = "occurredAt", source = "occurredAt")
    ValidateProposalCommand toValidateProposalCommand(UUID proposalId, ValidateProposalRequest request, Instant occurredAt);

    @Mapping(target = "proposalId", source = "proposalId")
    @Mapping(target = "occurredAt", source = "occurredAt")
    RejectProposalCommand toRejectProposalCommand(UUID proposalId, RejectProposalRequest request, Instant occurredAt);

    @Mapping(target = "proposalId", source = "proposalId")
    @Mapping(target = "occurredAt", source = "occurredAt")
    SelectDebtCommand toSelectDebtCommand(UUID proposalId, SelectDebtRequest request, Instant occurredAt);

    @Mapping(target = "proposalId", source = "proposalId")
    @Mapping(target = "occurredAt", source = "occurredAt")
    MarkUnmatchedCommand toMarkUnmatchedCommand(UUID proposalId, MarkUnmatchedRequest request, Instant occurredAt);

    @Mapping(target = "proposalId", source = "proposalId")
    @Mapping(target = "occurredAt", source = "occurredAt")
    RequestInvestigationCommand toRequestInvestigationCommand(
            UUID proposalId,
            RequestInvestigationRequest request,
            Instant occurredAt);

    @Mapping(target = "id", expression = "java(proposal.id())")
    @Mapping(target = "status", expression = "java(proposal.status())")
    @Mapping(target = "matchingMethod", expression = "java(proposal.matchingMethod())")
    @Mapping(target = "reason", expression = "java(unwrap(proposal.reason()))")
    @Mapping(target = "createdAt", expression = "java(proposal.createdAt())")
    @Mapping(target = "updatedAt", expression = "java(proposal.updatedAt())")
    AllocationProposalSummaryResponse toAllocationProposalSummaryResponse(AllocationProposal proposal);

    @Mapping(target = "id", expression = "java(proposal.id())")
    @Mapping(target = "paymentId", expression = "java(proposal.paymentId())")
    @Mapping(target = "status", expression = "java(proposal.status())")
    @Mapping(target = "matchingMethod", expression = "java(proposal.matchingMethod())")
    @Mapping(target = "reason", expression = "java(unwrap(proposal.reason()))")
    @Mapping(target = "validatedBy", expression = "java(unwrap(proposal.validatedBy()))")
    @Mapping(target = "validatedAt", expression = "java(unwrap(proposal.validatedAt()))")
    @Mapping(target = "selectedDebtId", expression = "java(unwrap(proposal.selectedDebtId()))")
    @Mapping(target = "candidates", ignore = true)
    @Mapping(target = "version", expression = "java(proposal.version())")
    @Mapping(target = "createdAt", expression = "java(proposal.createdAt())")
    @Mapping(target = "updatedAt", expression = "java(proposal.updatedAt())")
    AllocationProposalResponse toAllocationProposalResponseWithoutCandidates(AllocationProposal proposal);

    default AllocationProposalResponse toAllocationProposalResponse(
            AllocationProposal proposal,
            List<AllocationProposalCandidate> candidates) {
        AllocationProposalResponse base = toAllocationProposalResponseWithoutCandidates(proposal);
        List<AllocationProposalCandidateResponse> candidateResponses = candidates == null
                ? List.of()
                : candidates.stream().map(this::toAllocationProposalCandidateResponse).toList();
        return new AllocationProposalResponse(
                base.id(),
                base.paymentId(),
                base.status(),
                base.matchingMethod(),
                base.reason(),
                base.validatedBy(),
                base.validatedAt(),
                base.selectedDebtId(),
                candidateResponses,
                base.version(),
                base.createdAt(),
                base.updatedAt());
    }

    @Mapping(target = "id", expression = "java(candidate.id())")
    @Mapping(target = "debtorId", expression = "java(candidate.debtorId())")
    @Mapping(target = "debtId", expression = "java(candidate.debtId())")
    @Mapping(target = "confidence", expression = "java(candidate.confidence())")
    @Mapping(target = "suggestedAmount", expression = "java(candidate.suggestedAmount())")
    @Mapping(target = "rankOrder", expression = "java(candidate.rankOrder())")
    AllocationProposalCandidateResponse toAllocationProposalCandidateResponse(AllocationProposalCandidate candidate);

    @Mapping(target = "id", expression = "java(proposal.id())")
    @Mapping(target = "status", expression = "java(proposal.status())")
    @Mapping(target = "reason", expression = "java(unwrap(proposal.reason()))")
    @Mapping(target = "validatedBy", expression = "java(unwrap(proposal.validatedBy()))")
    @Mapping(target = "validatedAt", expression = "java(unwrap(proposal.validatedAt()))")
    @Mapping(target = "selectedDebtId", expression = "java(unwrap(proposal.selectedDebtId()))")
    @Mapping(target = "updatedAt", expression = "java(proposal.updatedAt())")
    ProposalStateResponse toProposalStateResponse(AllocationProposal proposal);

    @Mapping(target = "allocationId", expression = "java(allocation.id())")
    @Mapping(target = "proposalId", expression = "java(unwrap(allocation.proposalId()))")
    @Mapping(target = "paymentId", expression = "java(allocation.paymentId())")
    @Mapping(target = "debtId", expression = "java(allocation.debtId())")
    @Mapping(target = "amount", expression = "java(allocation.amount())")
    @Mapping(target = "status", expression = "java(allocation.status().name())")
    @Mapping(target = "createdBy", expression = "java(allocation.createdBy())")
    @Mapping(target = "createdAt", expression = "java(allocation.createdAt())")
    AllocationResultResponse toAllocationResultResponse(PaymentAllocation allocation);

    default AllocationProposalListResponse toAllocationProposalListResponse(UUID paymentId, List<AllocationProposal> proposals) {
        List<AllocationProposalSummaryResponse> responses = proposals.stream()
                .map(this::toAllocationProposalSummaryResponse)
                .toList();
        return new AllocationProposalListResponse(paymentId, responses);
    }

    default <T> T unwrap(Optional<T> value) {
        return value == null ? null : value.orElse(null);
    }
}
