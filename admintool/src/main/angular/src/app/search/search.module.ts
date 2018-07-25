import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../base/shared/shared.module';
import { SearchService } from './services/search.service';
import { FedoraRestService } from './services/fedora.rest.service';
import { ElasticSearchService } from './services/elastic-search.service';

import { SearchRoutingModule } from './search-routing.module';
import { SearchComponent } from './search.component';
import { UserSearchComponent } from './user-search/user-search.component';
import { OrganizationSearchComponent } from './organization-search/organization-search.component';
import { ContextSearchComponent } from './context-search/context-search.component';
import { ItemSearchComponent } from './item-search/item-search.component';
import { SearchTermComponent } from './search-term/search-term.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    SearchRoutingModule
  ],
  declarations: [
    SearchComponent,
    UserSearchComponent,
    OrganizationSearchComponent,
    ContextSearchComponent,
    ItemSearchComponent,
    SearchTermComponent
  ],
  entryComponents: [
    SearchTermComponent
  ],
  providers: [
    ElasticSearchService,
    SearchService,
    FedoraRestService
  ]
})
export class SearchModule { }
