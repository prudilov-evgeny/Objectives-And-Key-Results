package ru.cohenrol.objectivesandkeyresults.web.mapper;

import org.mapstruct.Mapper;
import ru.cohenrol.objectivesandkeyresults.domain.model.response.GetIssuesResponse;
import ru.cohenrol.objectivesandkeyresults.domain.model.response.GetRunsResponse;
import ru.cohenrol.objectivesandkeyresults.domain.model.response.GetSummaryResponse;
import ru.cohenrol.objectivesandkeyresults.web.model.response.GetIssuesResponseDto;
import ru.cohenrol.objectivesandkeyresults.web.model.response.GetRunsResponseDto;
import ru.cohenrol.objectivesandkeyresults.web.model.response.GetSummaryResponseDto;

@Mapper(componentModel = "spring")
public interface WebMapper {
    GetSummaryResponseDto toDto(GetSummaryResponse getSummaryResponse);
    GetRunsResponseDto toDto(GetRunsResponse getRunsResponseDto);
    GetIssuesResponseDto toDto(GetIssuesResponse getIssuesResponse);
}
