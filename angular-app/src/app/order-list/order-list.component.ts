import {Component, OnDestroy, OnInit} from '@angular/core';
import {OrderApi} from "../api/order-api.service";
import {OrderDto} from "../model/order-dto";
import {timer} from 'rxjs';
import {OrderService} from "../service/order.service";
import {AudioService} from "../service/audio.service";

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit, OnDestroy {
  subscription;

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  constructor(private orderApi: OrderApi,
              private orderService: OrderService,
              private audioService: AudioService) {
    // const t = timer(2000, 4000);
    // this.subscription = t.subscribe(v => {
    //   this.loadData();
    // });
  }

  scoreBoardNumberOflastReadyOrder: string;
  readyOrders: OrderDto[];
  inProgressOrders: OrderDto[];

  ngOnInit() {
    var that = this;
    this.orderApi.updates.subscribe({
      next(update) {
        console.log('Received order update in component: ', update);
        that.loadData();
      },
      error(msg) {
        console.log('order update in component: ', msg);
      }
    })
    this.loadData();
  }

  loadData() {
    this.orderApi.findAll().subscribe(data => {
      this.inProgressOrders = this.orderService.filterAndSort(data, 'IN_PROGRESS');
      this.readyOrders = this.orderService.filterAndSort(data, "READY");
      let newScoreboardNumber = this.readyOrders.length > 0 ? this.readyOrders[0].scoreBoardNumber.toString() : '-';

      let didScoreboardNumberChanged = this.scoreBoardNumberOflastReadyOrder !== newScoreboardNumber;
      if (this.scoreBoardNumberOflastReadyOrder && newScoreboardNumber !== '-' && didScoreboardNumberChanged) {
        this.audioService.playSound(newScoreboardNumber);
      }
      this.scoreBoardNumberOflastReadyOrder = newScoreboardNumber;
    });
  }
}
