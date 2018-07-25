import { Component, OnInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Subscription } from 'rxjs';

import { MessagesService } from '../../base/services/messages.service';
import { IndicesService } from '../indices-services/indices.service';
import { NgForm } from '@angular/forms';

@Component({
  // selector: 'app-indices-detail',
  templateUrl: './indices-detail.component.html',
  styleUrls: ['./indices-detail.component.scss']
})
export class IndicesDetailComponent implements OnInit {

  @ViewChild('f') form: NgForm;
  index_name;
  isNewIndex: boolean = false;
  settings;
  settings2selectFrom: string[] = ['default', 'minimal', 'with_auto_suggest'];
  selectedSettings;
  mapping;
  mappings2selectFrom: string[] = ['none', 'eins', 'zwei', 'drei', 'vier'];
  selectedMapping;
  subscription: Subscription;

  constructor(private route: ActivatedRoute,
    private router: Router,
    private service: IndicesService,
    private message: MessagesService) { }

  ngOnInit() {
    this.subscription = this.route.params
      .subscribe(params => {
        const name = params['name'];
        this.index_name = name;
      });
    if (this.index_name !== 'new') {
      this.service.getSettings4Index(this.index_name, settings => {
        this.settings = settings;
      });
      this.service.getMapping4Index(this.index_name, 'item', mapping => {
        this.mapping = mapping;
      });
    } else {
      this.isNewIndex = true;
    }
  }

  onChangeSettings(setting) {
    this.settings = setting;
    alert(this.settings)
  }

  onChangeMappings(mapping) {
    this.mapping = mapping;
    alert(this.mapping)
  }

  gotoList() {
    this.router.navigate(['more/list']);
  }

  save() {
    if (this.form.valid) {
    if (confirm('saving ' + this.index_name)) {
      this.service.create(this.index_name, null, index => {
        this.message.success('created index ' + index.index + '   ' + index.acknowledged);
      });
    }
  } else {
    alert('OOOPS!')
  }
  }
}
