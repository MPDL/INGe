import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from 'elasticsearch';

import { MessagesService } from '../../base/services/messages.service';
import { environment } from '../../../environments/environment';

@Injectable()
export class ElasticService {

  client: Client;
  uri: string;

  constructor(private message: MessagesService,
    private http: HttpClient) {
    if (!this.client) {
      this.connect();
    }
  }

  private connect() {
    const url = environment.elastic_url;
    this.client = new Client({
      host: url,
      log: ['error', 'warning']
    });
  }

  public connect2(url) {
    this.client = new Client({
      host: url,
      log: ['error', 'warning']
    });
  }

  info_api() {
    return this.client.info({});
  }

  listAllIndices() {
    return this.client.cat.indices({ format: 'json' });
  }

  listAliases() {
    return this.client.indices.getAlias({});
  }

  create(name, body) {
    return this.client.indices.create({
      index: name,
      body: body
    });
  }

  delete(name) {
    return this.client.indices.delete({
      index: name
    });
  }

  getMapping4Index(index: string) {
    return this.client.indices.getMapping({
      index: index
    });
  }

  putMapping2Index(index: string, type: string, mapping: object) {
    return this.client.indices.putMapping({
      index: index,
      type: type,
      body: mapping
    });
  }

  getSettings4Index(index: string) {
    return this.client.indices.getSettings({
      index: index
    });
  }

  getOuById(id) {
    return this.client.get({
      index: 'new_model_ous',
      type: 'organization',
      id: id
    });
  }

  getChildren4OU(id: string) {
    return this.client.search({
      index: 'new_model_ous',
      body: '{"query": {"term": {"parentAffiliation.objectId":"' + id + '"}}}',
      size: 100,
      sort: 'name.keyword'
    });
  }

  searchChildren(id, callback): any {
    return this.client.search({
      index: 'new_model_ous',
      body: '{"size":100, "query": {"term": {"parentAffiliation.objectId":"' + id + '"}}}',
    }, (error, response) => {
      if (error) {
        this.message.error(error)
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
}
