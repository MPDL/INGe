import { Injectable } from '@angular/core';
import { ElasticService } from '../../base/services/elastic.service';
import { MessagesService } from '../../base/services/messages.service';
import { environment } from '../../../environments/environment';

@Injectable()
export class Elastic4contextsService extends ElasticService {

  constructor(messages: MessagesService) { super(messages) }

  contexts4auto(term, callback) {
        const contexts = Array<any>();
        if (term) {
            this.client.search({
                index: environment.ctx_index.name,
                q: 'name.auto:' + term,
                sort: 'name.keyword:asc',
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

    ous4auto(term, callback) {
        const ous = Array<any>();
        if (term) {
            this.client.search({
                index: environment.ou_index.name,
                // q: 'metadata.name.auto:' + term,
                body: term,
                sort: 'metadata.name.keyword:asc',
                size: 25
            }, (error, response) => {
                if (error) {
                    this.messages.error(error);
                } else {
                    response.hits.hits.forEach(hit => {
                        const ouname = JSON.parse(JSON.stringify(hit._source));
                        ous.push(ouname);
                    });
                    callback(ous);
                }
            });
        }
    }
}
