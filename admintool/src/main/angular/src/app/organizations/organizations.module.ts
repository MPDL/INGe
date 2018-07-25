import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatTreeModule } from '@angular/material';
import { CdkTreeModule } from '@angular/cdk/tree';
import { SharedModule } from '../base/shared/shared.module';
import { OrganizationsRoutingModule } from './organizations-routing.module';
import { OrganizationListComponent } from './organization-list/organization-list.component';
import { OrganizationDetailsComponent } from './organization-details/organization-details.component';
import { OrganizationsService } from './services/organizations.service';
import { Elastic4ousService } from './services/elastic4ous.service';
import { OrganizationTreeComponent } from './organization-tree/organization-tree.component';

@NgModule({
  imports: [
    FormsModule,
    CommonModule,
    MatTreeModule,
    CdkTreeModule,
    SharedModule,
    OrganizationsRoutingModule
  ],
  declarations: [
    OrganizationListComponent,
    OrganizationDetailsComponent,
    OrganizationTreeComponent
  ],
  providers: [
    OrganizationsService,
    Elastic4ousService
  ]
})
export class OrganizationsModule { }
