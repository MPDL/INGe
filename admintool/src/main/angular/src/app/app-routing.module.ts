import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './base/home/home.component';
import { PageNotFoundComponent } from './base/common/page-not-found.component';

const routes: Routes = [
  {
    path: 'home', component: HomeComponent
  },
  {
    path: '', redirectTo: '/home', pathMatch: 'full'
  },
  {
    path: '**', component: PageNotFoundComponent
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
