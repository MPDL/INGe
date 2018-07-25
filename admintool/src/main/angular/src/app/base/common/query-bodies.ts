export const mpgOus4auto = {
    'size': 25,
    'query': {
        'bool': {
            'filter': {
                'terms': {
                    'parentAffiliation.objectId': ['ou_persistent13', 'ou_persistent22']
                }
            },
            'must': {
                'term': {
                    'metadata.name.auto': 'term'
                }
            }
        }
    },
    'sort': [
        { 'metadata.name.keyword': { 'order': 'asc' } }
    ]
};
export const allOpenedOUs = {
    'size': 300,
    'query': {
        'bool': {
            'filter': 
                {
                    'terms': {
                        'parentAffiliation.objectId': ['ou_persistent13', 'ou_persistent22']
                    }
                },
            'must': {
                'term': {
                    'publicStatus.keyword': 'OPENED'
                }
            }
        }
    },
    'sort': [
        { 'parentAffiliation.objectId': { 'order': 'asc'} },
        { 'metadata.name.keyword': { 'order': 'asc' } }
    ]
};


