import { Injectable } from '@angular/core';
import { MessagesService } from '../../base/services/messages.service';
import { ElasticService } from '../../base/services/elastic.service';

@Injectable()
export class IndicesService extends ElasticService {

    constructor(messages: MessagesService) {super(messages)}

    create(name, body, callback) {
        this.client.indices.create({
            index: name,
            body: body
        }, (err, res) => {
            if (err) {
                this.messages.error(err);
            } else {
                callback(res);
            }
        });
    }

    delete(name, callback) {
        this.client.indices.delete({
            index: name
        }, (err, res) => {
            if (err) {
                this.messages.error(err);
            } else {
                callback(res);
            }
        });
    }

    listAllIndices(callback) {
        this.client.cat.indices({
            format: 'json'
        }, (err, res) => {
            if (err) {
                this.messages.error(err);
            } else {
                callback(res);
            }
        });
    }

    getMapping4Index(index: string, type: string, callback) {
        this.client.indices.getMapping({
            index: index,
            // type: type,
        }, (err, res) => {
            if (err) {
                this.messages.error(err);
            } else {
                callback(res);
            }
        });
    }

    getSettings4Index(index: string, callback) {
        this.client.indices.getSettings({
            index: index
        }, (err, res) => {
            if (err) {
                this.messages.error(err);
            } else {
                callback(res);
            }
        });
    }

    localNodeInfo(callback) {
        this.client.cluster.state({metric: ['master_node', 'nodes']},
        (err, res) => {
            if (err) {
                this.messages.error(err);
            } else {
                callback(res);
            }
        });
    }
}
