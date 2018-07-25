export const environment = {
  production: false,
  base_url: 'https://dev.inge.mpdl.mpg.de',
  elastic_url: 'https://dev.inge.mpdl.mpg.de/inge',
  rest_url: 'https://dev.inge.mpdl.mpg.de/rest',
  rest_users: '/users',
  rest_ous: '/ous',
  rest_contexts: '/contexts',
  rest_items: '/items',
  item_index: {
    name: 'new_model_items',
    type: 'item'
  },
  user_index: {
    name: 'new_model_users',
    type: 'user'
  },
  ou_index: {
    name: 'new_model_ous',
    type: 'organization'
  },
  ctx_index: {
    name: 'new_model_contexts',
    type: 'context'
  },
  elastic_admin: 'devil',
  blazegraph_sparql_url: 'http://localhost:8888/blazegraph/namespace/wf/sparql',

};
