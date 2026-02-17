function prepareOUSuggestRequest(q) {
    var elquery = {
        "query":
            {
                "multi_match":
                    {
                        "query":q,
                        "type":"bool_prefix",
                        "fields":["metadata.name.autosuggest","metadata.name.autosuggest._2gram","metadata.name.autosuggest._3gram","metadata.alternativeNames.autosuggest","metadata.alternativeNames.autosuggest._2gram","metadata.alternativeNames.autosuggest._3gram"]
                    }
            },
        "size": 50
    }
    return {
        "method" : "POST",
        "contentType" : "application/json",
        "data" : JSON.stringify(elquery)
    }
}

function handleOUSuggestResponse(data) {
    const res = data?.hits?.hits?.map(hit => {
        return {
            "id" : hit._source.objectId,
            "value" : hit._source.namePath?.join(', ') || hit._source.name
        }
    })
    return res || [];
}