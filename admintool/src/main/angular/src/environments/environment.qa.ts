export const environment = {
  production: false,
  base_url: 'https://qa.inge.mpdl.mpg.de',
  elastic_url: 'https://qa.inge.mpdl.mpg.de/es',
  rest_url: 'https://qa.inge.mpdl.mpg.de/rest',
  rest_users: '/users',
  rest_ous: '/ous',
  rest_contexts: '/contexts',
  rest_items: '/items',
  item_index: {
    name: 'items',
    type: 'item'
  },
  user_index: {
    name: 'users',
    type: 'user'
  },
  ou_index: {
    name: 'ous',
    type: 'organization'
  },
  ctx_index: {
    name: 'contexts',
    type: 'context'
  },
  elastic_admin: 'devil',
  blazegraph_sparql_url: 'http://localhost:8888/blazegraph/namespace/wf/sparql',

};
