import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SearchComponent } from './search.component';
import { UserSearchComponent } from './user-search/user-search.component';
import { OrganizationSearchComponent } from './organization-search/organization-search.component';
import { ContextSearchComponent } from './context-search/context-search.component';
import { ItemSearchComponent } from './item-search/item-search.component';
import { AdminGuard } from '../base/services/admin-guard.service';

const routes: Routes = [
  {
    path: 'search',
    component: SearchComponent,
    // canActivate: [ AdminGuard ],
    children: [
      {
        path: '',
        // canActivateChild: [ AdminGuard ],
        children: [
          { path: 'users', component: UserSearchComponent, canActivate: [AdminGuard] },
          { path: 'organizations', redirectTo: '/organizations', pathMatch: 'full' },
        //  { path: 'organizations', component: OrganizationSearchComponent },
          { path: 'contexts', redirectTo: '/contexts', pathMatch: 'full' },
          { path: 'items', component: ItemSearchComponent},
          { path: '', component: ContextSearchComponent }
        ]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SearchRoutingModule { }
