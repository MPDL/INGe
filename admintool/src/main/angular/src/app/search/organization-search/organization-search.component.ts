import { Component, OnInit } from '@angular/core';

import { FedoraRestService } from '../services/fedora.rest.service';

@Component({
  selector: 'app-organization-search',
  templateUrl: './organization-search.component.html',
  styleUrls: ['./organization-search.component.scss']
})
export class OrganizationSearchComponent implements OnInit {

  result;
  id;
  mems;

  constructor(protected fedora: FedoraRestService) { }

  ngOnInit() {
    this.fedora.getResource()
      .subscribe(response => {
      this.result = response;
        this.id = this.result[0]['@id'];
        this.mems = this.result[0]['http://www.w3.org/ns/ldp#contains'][0]['@id'];
      }, error => console.log(error));
  }

}
