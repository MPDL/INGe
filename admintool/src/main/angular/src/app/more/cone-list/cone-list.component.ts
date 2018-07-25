import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { ConeService } from '../indices-services/cone.service';
import { BlazegraphService } from '../indices-services/blazegraph.service';


@Component({
  selector: 'app-cone-list',
  templateUrl: './cone-list.component.html',
  styleUrls: ['./cone-list.component.scss']
})
export class ConeListComponent implements OnInit {

  answer: any[] = [];
  graphIRIs: any[] = [];
  insertGraphIRI;
  fromGraphIRI;
  insertResult: any;
  uri2insert: string;
  describeUri: string;

  constructor(private svc: ConeService,
    private blz: BlazegraphService) { }

  ngOnInit() {
    // this.svc.getAllJournals().subscribe(data => this.answer = data);
    // this.blz.describeResource('http://d-nb.info/gnd/118715615').subscribe(data => this.answer = data);
    this.blz.getNamedGraphs().subscribe(iris => {
      iris.forEach(iri => this.graphIRIs.push(iri.g.value));
    });
  }

  search4URI(uri, graphIRI) {
    // this.fromGraphIRI = graphIRI.g.value;
    this.blz.describeResource(uri, graphIRI).subscribe(data => {
      this.answer = data;
      alert(data.length);
    });
  }
  insertURI(uri, graphIRI) {
    // this.insertGraphIRI = graphIRI.g.value;
    this.blz.insertResourceFromURI(uri, graphIRI).subscribe(data => this.insertResult = data);
  }

  // get diagnostic() {return JSON.stringify(this.answer)};

}
