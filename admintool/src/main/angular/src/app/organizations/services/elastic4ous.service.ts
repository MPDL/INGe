import { Injectable } from '@angular/core';

import { ElasticService } from '../../base/services/elastic.service';
import { MessagesService } from '../../base/services/messages.service';
import { environment } from '../../../environments/environment';

@Injectable()
export class Elastic4ousService extends ElasticService {

  constructor(messages: MessagesService) { super(messages) }

  ous4auto(term, callback) {
        const contexts = Array<any>();
        if (term) {
            this.client.search({
                index: environment.ou_index.name,
                q: 'metadata.name.auto:' + term,
                sort: 'metadata.name.keyword:asc',
                size: 25
            }, (error, response) => {
                if (error) {
                    this.messages.error(error);
                } else {
                    response.hits.hits.forEach(hit => {
                        const ctxname = JSON.parse(JSON.stringify(hit._source));
                        contexts.push(ctxname);
                    });
                    callback(contexts);
                }
            });
        }
    }

  listOuNames(parent: string, id: string, callback): any {
    let queryString: string;
    if (parent.match('parent')) {
      queryString = 'parentAffiliation.objectId:*' + id;
    } else if (parent.match('predecessor')) {
      queryString = 'objectId:*' + id;
    }

    if (queryString.length > 0) {
      return this.client.search({
        index: environment.ou_index.name,
        // q: 'parentAffiliation.objectId:*' + parent,
        q: queryString,
        _sourceInclude: 'objectId, metadata.name, hasChildren, publicStatus',
        size: 100,
        sort: 'metadata.name.keyword:asc'
      },
        (error, response) => {
          if (error) {
            this.messages.error(error);
          }
          if (response) {
            const hitList = Array<any>();
            response.hits.hits.forEach((hit) => {
              const source = JSON.stringify(hit._source);
              const json = JSON.parse(source);
              hitList.push(json);
            });
            callback(hitList)
          }
        });
    }
  }

  searchOu(id, callback): any {
    return this.client.search({
      index: environment.ou_index.name,
      q: `objectId:*${id}`,
    }, (error, response) => {
      if (error) {
        this.messages.error(error)
      }
      if (response) {
        const hitList = Array<any>();
        response.hits.hits.forEach((hit) => {
          const source = JSON.stringify(hit._source);
          const json = JSON.parse(source);
          hitList.push(json);
        })
        callback(hitList)
      }
    })
  }

  updateOu(ou) {
    this.client.index({
      index: environment.ou_index.name,
      type: environment.ou_index.type,
      id: ou.objectId,
      body: ou
    }, (error, response) => {
      if (error) {
        this.messages.error(error);
      } else {
        // this.message.success(JSON.stringify(response, null, 4));
        this.messages.success(response.result);
      }
    });
  }

  getOuById(id) {
    return this.client.get({
      index: environment.ou_index.name,
      type: 'organization',
      id: id
    });
  }

  getChildren4OU(id: string) {
    return this.client.search({
      index: environment.ou_index.name,
      body: '{"query": {"term": {"parentAffiliation.objectId":"' + id + '"}}}',
      size: 100,
      sort: 'name.keyword'
    });
  }
}
