import { Component, OnInit } from '@angular/core';
import * as AOS from 'aos'; 


@Component({
  selector: 'app-home-front',
  templateUrl: './home-front.component.html',
  styleUrls: ['./home-front.component.css']
})
export class HomeFrontComponent implements OnInit {

  constructor() {}

  ngOnInit(): void {
    AOS.init();
    console.log('HomeFrontComponent Loaded ✅');
  }

}
