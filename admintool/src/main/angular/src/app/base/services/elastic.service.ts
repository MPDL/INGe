import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Client, SearchResponse, GetResponse } from 'elasticsearch';
import { environment } from '../../../environments/environment';
import { MessagesService } from '../services/messages.service';


@Injectable()
export class ElasticService {

  public client: Client;
  public uri: string = environment.elastic_url;

  constructor(protected messages: MessagesService) {
    if (!this.client) {
      this.connect();
    }
  }

  private connect() {
    this.client = new Client({
      host: this.uri,
      log: ['error', 'warning']
    });
  }

  count(index: string, callback): any {
    return this.client.search({
      index: index,
      size: 0
    },
      (error, response) => {
        if (error) {
          this.messages.error(error);
        }
        if (response) {
          callback(response.hits.total);
        }
      });
  }

  listOuNames(parent: string, id: string, callback): any {
    let queryString: string;
    if (parent.match('parent')) {
      queryString = 'parentAffiliation.objectId:*' + id + ' AND publicStatus:OPENED';
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


}
