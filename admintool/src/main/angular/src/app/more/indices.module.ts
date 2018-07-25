import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from '../base/shared/shared.module';
import { IndicesRoutingModule } from './indices-routing.module';
import { IndicesService } from './indices-services/indices.service';
import { ConeService } from './indices-services/cone.service';
import { BlazegraphService } from './indices-services/blazegraph.service';
import { IndicesComponent } from './indices.component';
import { IndicesDashboardComponent } from './indices-dashboard/indices-dashboard.component';
import { IndicesListComponent } from './indices-list/indices-list.component';
import { IndicesDetailComponent } from './indices-detail/indices-detail.component';
import { ConeListComponent } from './cone-list/cone-list.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    IndicesRoutingModule
  ],
  declarations: [IndicesComponent, IndicesDashboardComponent, IndicesListComponent, IndicesDetailComponent, ConeListComponent],
  providers: [ IndicesService, ConeService, BlazegraphService ]
})
export class IndicesModule { }
