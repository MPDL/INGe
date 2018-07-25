import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { IndicesComponent } from './indices.component';
import { IndicesDashboardComponent } from './indices-dashboard/indices-dashboard.component';
import { IndicesListComponent } from './indices-list/indices-list.component';
import { IndicesDetailComponent } from './indices-detail/indices-detail.component';
import { ConeListComponent } from './cone-list/cone-list.component';


const routes: Routes = [
  {
    path: 'more',
    component: IndicesComponent,
    children: [
      {
        path: '',
        // canActivateChild: [ AdminGuard ],
        children: [
          { path: 'list', component: IndicesListComponent },
          { path: 'list/:name', component: IndicesDetailComponent },
          { path: 'cone', component: ConeListComponent },
          { path: '', component: IndicesDashboardComponent }
        ]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: []
})
export class IndicesRoutingModule { }
