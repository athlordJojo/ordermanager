import {Component, OnDestroy} from '@angular/core';
import {OrderApi} from "../api/order-api.service";
import {OrderDto, OrderState} from "../model/order-dto";
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

  private readonly initMainScoreBoardNumber: string = "_"
  mainScoreBoardNumber: string;
  chunkOfReadyOrders: Array<Array<OrderDto>>;
  chunksOfInProgressOrders: Array<Array<OrderDto>>;

  ngOnDestroy(): void {
    console.debug("destroy")
    // TODO seems not to work. Multiple subscriber are alive after moving from one path to another
    this.orderUpdateSubscription.unsubscribe();
  }

  constructor(private orderApi: OrderApi,
              private orderService: OrderService,
              private audioService: AudioService) {
    var that = this;
    this.mainScoreBoardNumber = this.initMainScoreBoardNumber
    this.chunkOfReadyOrders = [];
    this.chunksOfInProgressOrders = [];
    this.orderUpdateSubscription = this.orderApi.updates.subscribe({
      next(update) {
        console.log('Received order update in component: ', update);
        that.loadData();
      },
      error(msg) {
        console.error('error order update in component: ', msg);
      }
    })
    this.loadData();
  }

  loadData() {
    this.orderApi.findAll().subscribe(data => {
      let allInProgressOrders = this.orderService.filterAndSortByModifiedDate(data, OrderState.IN_PROGRESS)
      let allReadyOrders = this.orderService.filterAndSortByModifiedDate(data, OrderState.READY)
      let newScoreboardNumber = allReadyOrders.length > 0 ? allReadyOrders[allReadyOrders.length - 1].scoreBoardNumber.toString() : '-';

      // create a subarray containing orderDto with a size of 4 elements
      this.chunksOfInProgressOrders = this.orderService.sliceIntoSubArrays(allInProgressOrders);
      this.chunkOfReadyOrders = this.orderService.sliceIntoSubArrays(allReadyOrders);

      let didScoreboardNumberChanged = this.mainScoreBoardNumber !== newScoreboardNumber;
      let isFirstRun = this.mainScoreBoardNumber === this.initMainScoreBoardNumber
      if (!isFirstRun && newScoreboardNumber !== '-' && didScoreboardNumberChanged) {
        this.audioService.playSound(newScoreboardNumber);
      }
      this.mainScoreBoardNumber = newScoreboardNumber;
    });
  }
}
