import {ApplicationRef, ChangeDetectorRef, Component, NgZone, OnDestroy, OnInit} from '@angular/core';
import {OrderApi} from "../api/order-api.service";
import {OrderDto} from "../model/order-dto";
import {OrderService} from "../service/order.service";
import {AudioService} from "../service/audio.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnDestroy {
  private orderUpdateSubscription: Subscription;

  scoreBoardNumberOflastReadyOrder: string;
  chunkOfReadyOrders: Array<Array<OrderDto>>;
  chunksOfInProgressOrders: Array<Array<OrderDto>>;

  ngOnDestroy(): void {
    console.debug("destroy")
    // TODO seems not to work. Multiple subscriber are alive after moving from one  path to another
    this.orderUpdateSubscription.unsubscribe();
  }

  constructor(private orderApi: OrderApi,
              private orderService: OrderService,
              private audioService: AudioService) {
    var that = this;
    this.orderUpdateSubscription = this.orderApi.updates.subscribe({
      next(update) {
        console.log('Received order update in component: ', update);
        that.loadData();
      },
      error(msg) {
        console.error('order update in component: ', msg);
      }
    })
    this.loadData();
  }


  loadData() {
    this.orderApi.findAll().subscribe(data => {
      let allInProgressOrders = this.orderService.filterAndSort(data, 'IN_PROGRESS');
      let allReadyOrders = this.orderService.filterAndSort(data, "READY");
      let newScoreboardNumber = allReadyOrders.length > 0 ? allReadyOrders[0].scoreBoardNumber.toString() : '-';

      // create a subarray containing orderDto with a size of 5 elements
      this.chunksOfInProgressOrders = this.orderService.sliceIntoSubArrays(allInProgressOrders);
      this.chunkOfReadyOrders = this.orderService.sliceIntoSubArrays(allReadyOrders);

      let didScoreboardNumberChanged = this.scoreBoardNumberOflastReadyOrder !== newScoreboardNumber;
      if (this.scoreBoardNumberOflastReadyOrder && newScoreboardNumber !== '-' && didScoreboardNumberChanged) {
        this.audioService.playSound(newScoreboardNumber);
      }
      this.scoreBoardNumberOflastReadyOrder = newScoreboardNumber;
    });
  }
}
