import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import * as bodyBuilder from 'bodybuilder';
import { PubmanRestService } from '../../base/services/pubman-rest.service';

@Injectable()
export class SearchService extends PubmanRestService {

  constructor(httpc: HttpClient) {
    super(httpc);
   }

  buildQueryOnly(request): any {
    let must, must_not, filter, should;
    request.searchTerms.forEach(element => {
      const field = element.field;
      const value: string = element.searchTerm;
      switch (element.type) {
        case 'must':
          if (must) {
            must.push({ match: { [field]: value } });
          } else {
            must = [{ match: { [field]: value } }];
          }
          break;
        case 'must_not':
          if (must_not) {
            must_not.push({ term: { [field]: value } });
          } else {
            must_not = [{ term: { [field]: value } }];
          }
          break;
        case 'filter':
          if (filter) {
            filter.push({ term: { [field]: value } });
          } else {
            filter = [{ term: { [field]: value } }];
          }
          break;
        case 'should':
          if (should) {
            should.push({ term: { [field]: value } });
          } else {
            should = [{ term: { [field]: value } }];
          }
          break;
        default:
      }
    });
    const body = { bool: { must, must_not, filter, should } };
    return body;
  }

  buildQuery(request, limit, offset, sortfield, ascdesc) {
    let query = bodyBuilder();

    request.searchTerms.forEach(element => {
      const field = element.field;
      const value: string = element.searchTerm;
      switch (element.type) {
        case 'must':
          query = query.query('match', field, value);
          break;
        case 'must_not':
          query = query.notFilter('term', field, value);
          break;
        case 'filter':
          query = query.filter('term', field, value);
          break;
        case 'should':
          query = query.orFilter('term', field, value);
          break;
        default:
      }
    });
    query = query.size(limit)
      .from(offset)
      .sort(sortfield, ascdesc);
    query = query.build();
    return query;
  }
}
