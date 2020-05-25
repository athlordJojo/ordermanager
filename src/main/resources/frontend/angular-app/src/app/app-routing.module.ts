import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {OrderListComponent} from "./order-list/order-list/order-list.component";
import {OrderFormComponent} from "./order-form/order-form/order-form.component";

const routes: Routes = [
  { path: 'orders', component: OrderListComponent },
  { path: 'addorder', component: OrderFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
