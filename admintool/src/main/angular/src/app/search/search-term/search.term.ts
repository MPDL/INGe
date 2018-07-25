export class SearchRequest {
    searchTerms: SearchTerm[];
}

export class SearchTerm {
    type: string;
    field: string;
    searchTerm: string;
    fields: string[];
}
