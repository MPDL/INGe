export class UserRO {
    objectId: string;
    name: string;
}

export class BasicRO {
    objectId: string;
    name: string;
    creationDate: Date;
    lastModificationDate: Date;
    creator: UserRO;
    modifier: UserRO;
}

export class Grant {
    role: string;
    grantType: string;
    objectRef: string;
}

export class User extends BasicRO {
    loginname: string;
    password: string;
    email: string;
    affiliation: BasicRO;
    active: boolean;
    grantList: Grant[];
}

export class OU extends BasicRO {
    predecessorAffiliations: BasicRO[];
    hasChildren: boolean = false;
    childAffiliations: BasicRO[];
    hasPredecessors: boolean = false;
    parentAffiliation: BasicRO;
    publicStatus: string;
    metadata: OUMetadata;
}

export class OUMetadata {
    alternativeNames: string[];
    city: string;
    countryCode: string;
    coordinates: Coordinates;
    identifiers: Identifier[];
    name: string;
    type: string;
    descriptions: string[];
    startDate: string;
    endDate: string;
}

export class Identifier {
    typeString: string;
    id: string;
}

export class Coordinates {
    altitude: number;
    latitude: number;
    longitude: number;

}

export class Context extends BasicRO {
    state: string;
    description: string;
    responsibleAffiliations: BasicRO[];
    allowedGenres: string[];
    allowedSubjectClassifications: string[];
    workflow: string;
    contactEmail: string;
}

export enum workflow {
    SIMPLE,
    STANDARD
}

export enum subjects {
    DDC,
    ISO639_3,
    JEL,
    MPINP,
    MPIPKS,
    MPIRG,
    MPIS_GROUPS,
    MPIS_PROJECTS,
    MPIWG_PROJECTS
}

export enum genres {
    ARTICLE,
    CONFERENCE_PAPER,
    BOOK_ITEM,
    THESIS,
    TALK_AT_EVENT,
    POSTER,
    BOOK,
    CONTRIBUTION_TO_COLLECTED_EDITION,
    REPORT,
    OTHER,
    PAPER,
    MEETING_ABSTRACT,
    BOOK_REVIEW,
    COURSEWARE_LECTURE,
    MONOGRAPH,
    COLLECTED_EDITION,
    PROCEEDINGS,
    CONTRIBUTION_TO_FESTSCHRIFT,
    CONTRIBUTION_TO_ENCYCLOPEDIA,
    NEWSPAPER_ARTICLE,
    CASE_NOTE,
    ISSUE,
    CONTRIBUTION_TO_COMMENTARY,
    CONFERENCE_REPORT,
    JOURNAL,
    CONTRIBUTION_TO_HANDBOOK,
    EDITORIAL,
    SERIES,
    PATENT,
    FILM,
    MANUSCRIPT,
    COMMENTARY,
    FESTSCHRIFT,
    OPINION,
    HANDBOOK,
    CASE_STUDY,
    ENCYCLOPEDIA,
    MANUAL,
    MULTI_VOLUME,
    NEWSPAPER
}

export class SearchResult {
    numberOfRecords: number;
    records: any[];
}

