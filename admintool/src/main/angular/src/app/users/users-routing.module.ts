import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserListComponent } from './user-list/user-list.component';
import { UserDetailsComponent } from './user-details/user-details.component';
import { UserDetailsResolverService } from './services/user-details-resolver.service';
import { AdminGuard } from '../base/services/admin-guard.service';
import { LoginGuard } from '../base/services/login-guard.service';

const routes: Routes = [
  { path: 'users',  component: UserListComponent },
  { path: 'user/:id', component: UserDetailsComponent, canActivate: [LoginGuard], resolve: {user: UserDetailsResolverService} }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [UserDetailsResolverService]
})
export class UsersRoutingModule { }
