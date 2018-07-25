import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AdminGuard } from '../base/services/admin-guard.service';
import { OrganizationListComponent } from './organization-list/organization-list.component';
import { OrganizationTreeComponent } from './organization-tree/organization-tree.component';

import { OrganizationDetailsComponent } from './organization-details/organization-details.component';
import { OrganizationDetailsResolverService } from './services/organization-details-resolver.service';

const routes: Routes = [
  {
    path: 'organizations',
    component: OrganizationTreeComponent
  },
  {
    path: 'organization/:id',
    component: OrganizationDetailsComponent,
    canActivate: [AdminGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [OrganizationDetailsResolverService]
})
export class OrganizationsRoutingModule { }
