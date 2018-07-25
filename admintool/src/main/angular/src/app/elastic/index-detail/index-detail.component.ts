import { Component, OnInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Subscription } from 'rxjs';

import { MessagesService } from '../../base/services/messages.service';
import { ElasticService } from '../service/elastic.service';
import { NgForm } from '@angular/forms';

@Component({
  // selector: 'app-indices-detail',
  templateUrl: './index-detail.component.html',
  styleUrls: ['./index-detail.component.scss']
})
export class IndexDetailComponent implements OnInit {

  @ViewChild('f') form: NgForm;
  index_name;
  isNewIndex: boolean = false;
  list: any[];
  settings;
  selectedSettings;
  mapping;
  selectedMapping;
  subscription: Subscription;

  constructor(private route: ActivatedRoute,
    private router: Router,
    private service: ElasticService,
    private message: MessagesService) { }

  ngOnInit() {
    this.subscription = this.route.params
      .subscribe(params => {
        const name = params['name'];
        this.index_name = name;
      });
    if (this.index_name !== 'new') {
      this.getSettings(this.index_name);
      this.getMapping(this.index_name);
    } else {
      this.isNewIndex = true;
      this.getList();
    }
  }

  async getSettings(index) {
    try {
      this.settings = await this.service.getSettings4Index(index);
    } catch (e) {
      this.message.error(e);
    }
  }

  async getMapping(index) {
    try {
      this.mapping = await this.service.getMapping4Index(index);
    } catch (e) {
      this.message.error(e);
    }
  }

  async getList() {
    try {
      this.list = await this.service.listAllIndices();
    } catch (e) {
      this.message.error(e);
    }
  }

  async onChangeSettings(index) {
    try {
      const settings = await this.service.getSettings4Index(index);
      this.selectedSettings = this.cloneSettings(settings[index]);
    } catch (e) {
      this.message.error(e);
    }
  }

  cloneSettings(settings) {
    if (settings.settings.index.version) { delete settings.settings.index.version; }
    if (settings.settings.index.provided_name) { delete settings.settings.index.provided_name; }
    if (settings.settings.index.creation_date) { delete settings.settings.index.creation_date; }
    if (settings.settings.index.uuid) { delete settings.settings.index.uuid; }
    return settings;
  }

  async onChangeMappings(index) {
    try {
      const mapping = await this.service.getMapping4Index(index);
      this.selectedMapping = mapping[index];
    } catch (e) {
      this.message.error(e);
    }
  }

  gotoList() {
    this.router.navigate(['elastic/index']);
  }

  async save() {
    if (this.form.valid) {
      let msg = 'saving ' + this.index_name + '\n';
      msg = msg.concat('with seleted settings / mapping');

      if (confirm(msg)) {
        const body = {};
        Object.assign(body, this.selectedSettings, this.selectedMapping);
        try {
          const res = await this.service.create(this.index_name, body);
          this.message.success('created index ' + this.index_name + '\n' + JSON.stringify(res));
        } catch (e) {
          this.message.error(e);
        }
      }
    } else {
      alert('OOOPS! ' + this.form.valid);
    }
  }
}
