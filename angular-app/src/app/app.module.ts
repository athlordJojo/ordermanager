import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {OrderListComponent} from './order-list/order-list.component';
import {OrderFormComponent} from './order-form/order-form.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {OrderApi} from "./api/order-api.service";
import {HttpClientModule} from "@angular/common/http";
import {AppRoutingModule} from "./app-routing.module";
import { HeaderComponent } from './header/header.component';

@NgModule({
  declarations: [
    AppComponent,
    OrderListComponent,
    OrderFormComponent,
    HeaderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [OrderApi],
  bootstrap: [AppComponent]
})
export class AppModule {
}
