import {Component, OnInit} from '@angular/core';
import {OrderApi} from '../api/order-api.service';
import {OrderDto} from "../model/order-dto";
import {FormGroup, FormControl, Validators, ValidatorFn, ValidationErrors} from '@angular/forms';
import {OrderService} from "../service/order.service";

@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',
  styleUrls: ['./order-form.component.css']
})
export class OrderFormComponent implements OnInit {
  scoreboardnumber: FormControl;

  ngOnInit(): void {
    this.loadData();
  }

  newOrderForm: FormGroup;
  selectedOrder: OrderDto;
  orders: OrderDto[] = [];

  constructor(
    private orderApi: OrderApi,
    private orderService: OrderService) {
    this.scoreboardnumber = new FormControl('', [
      Validators.required,
      Validators.min(0),
      this.uniqueScoreBoardValidator
    ]);
    this.newOrderForm = new FormGroup({
      scoreboardnumber: this.scoreboardnumber
    });
  }

  onSubmit() {
    this.orderApi.save(this.scoreboardnumber.value).subscribe(result => {
      this.scoreboardnumber.reset()
      this.loadData();
    });
  }

  uniqueScoreBoardValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const enteredValue = control.value;
    if (enteredValue === '' || !enteredValue) {
      return null;
    }
    let isScoreBoardNumberUsed = this.orderService.existOrderWithScoreboardNumber(this.orders, control.value);

    return isScoreBoardNumberUsed ? {'scoreBoardNumberIsUsed': true} : null;
  };


  loadData() {
    this.orderApi.findAll().subscribe(data => {
      this.orders = data;
      this.orders.sort((o1, o2) => {
        return o1.state.localeCompare(o2.state.toString())
      });
    })
  }

  selectOrder(selectedOrder: OrderDto) {
    // this toggles the selected item
    if (this.selectedOrder && this.selectedOrder.uuid == selectedOrder.uuid) {
      this.selectedOrder = null;
    } else {
      this.selectedOrder = selectedOrder;
    }
  }

  changeStateOfSelectedOrder(state: string) {
    this.selectedOrder.state = state;
    this.orderApi.update(this.selectedOrder).subscribe(data => this.loadData());
  }

  deleteSelectedOrder(order: OrderDto) {
    this.orderApi.delete(order).subscribe(
      data => {
        this.loadData();
      });
  }

  getClassForListItem(order: OrderDto) {
    let classForRow: string

    if (order === this.selectedOrder) {
      classForRow = "list-group-item-primary";
    } else {
      switch (order.state) {
        case "IN_PROGRESS": {
          classForRow = "list-group-item-secondary"
          break;
        }
        case "READY": {
          classForRow = "list-group-item-success"
          break;
        }
      }
    }
    return classForRow;
  }

  resetForm() {
    this.scoreboardnumber.reset();
  }
}
