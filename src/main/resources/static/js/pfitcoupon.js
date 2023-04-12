
$(document).ready(() => {
	fetchPcouponList(`/pcoupon/list`);
});

$('#search-coupon').on('input', function () {
	if ($(this).val() !== "" && parseInt($(this).val())) {
		let couponno = $(this).val();
		fetchPcouponList(`/pcoupon/getbyno?id=${couponno}`);
	} else {
		fetchPcouponList(`/pcoupon/list`);
	};
});

function fetchPcouponList(e) {
	fetch(e, {
		method: 'GET',
	}).then(response => response.json())
		.then(data => {
			data.reverse();
			const couponlist = document.querySelector('.all-promotion-list');
			couponlist.innerHTML = "";
			document.querySelector('.mb-4').textContent = "優惠券 (" + data.length + ")";
			const couponbody = data.map(obj => {

				return `<div class="main-card mt-4">
                  <div class="contact-list coupon-active">
                    <div class="top d-flex flex-wrap justify-content-between align-items-center p-4 border_bottom">
                      <div class="icon-box">
                        <span class="icon-big rotate-icon icon icon-purple">
                          <i class="fa-solid fa-ticket"></i>
                        </span>
                        <h5 class="font-18 mb-1 mt-1 f-weight-medium">${obj.pcouponno}<span class="font-weight-normal"> -
                        ${obj.pcouponname}</span></h5>
                        <p class="text-gray-50 m-0"><span class="visitor-date-time">${moment(obj.pcoupnsdate).format('YYYY-MM-DD HH:mm:ss')}
                          </span> - <span class="visitor-date-time">${moment(obj.pcoupnedate).format('YYYY-MM-DD HH:mm:ss')}</span></p>
                      </div>
                      <div class="d-flex align-items-center">
                        <label class="btn-switch tfs-8 mb-0 me-4 mt-1">
                        <input type="checkbox" onchange="changeStatus(this)" value="" ${obj.pcouponstatus === 1 ? 'checked' : 'false'}>
                          <span class="checkbox-slider"></span>
                        </label>
                        <div class="dropdown dropdown-default dropdown-text dropdown-icon-item">
                          <button class="option-btn-1" type="button" data-bs-toggle="dropdown" aria-haspopup="true"
                            aria-expanded="false">
                            <i class="fa-solid fa-ellipsis-vertical"></i>
                          </button>
                          <div class="dropdown-menu dropdown-menu-right">
                            <a href="#" class="dropdown-item" data-bs-toggle="modal"
														data-bs-target="#editcouponModal" onclick="editCoupon(${obj.pcouponno})"><i class="fa-solid fa-pen me-3"></i>編輯</a>
                            <a href="#" class="dropdown-item" onclick="saveCoupon(${obj.pcouponno}, event)"><i class="fa-solid fa-floppy-disk me-3"></i>儲存</a>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="bottom d-flex flex-wrap justify-content-between align-items-center p-4">
                      <div class="icon-box ">
                        <span class="icon">
                          <i class="fa-regular fa-circle-dot"></i>
                        </span>
                        <p>狀態</p>
                        <h6 class="coupon-status">${obj.pcouponstatus === 1 ? '可使用' : '不可使用'}</h6>
                      </div>
                      <div class="icon-box">
                        <span class="icon">
                          <i class="fa-solid fa-tag"></i>
                        </span>
                        <p>適用商品編號</p>
                        <h6 class="coupon-status">${obj.pfitcoupons && obj.pfitcoupons[0] && obj.pfitcoupons[0].pfitcouponNo.productno}</h6>
                      </div>
                      <div class="icon-box">
                        <span class="icon">
                          <i class="fa-solid fa-money-bill"></i>
                        </span>
                        <p>消費金額</p>
                        <h6 class="coupon-status">${obj.preachprice}</h6>
                      </div>
                      <div class="icon-box">
                        <span class="icon">
                          <i class="fa-solid fa-money-bill"></i>
                        </span>
                        <p>折扣</p>
                        <h6 class="coupon-status">${obj.pdiscount}</h6>
                      </div>
                    </div>
                  </div>
                </div>`;
			}).join('');
			couponlist.innerHTML += couponbody;
		})
}

function changeStatus(checkbox) {
	const boxstatus = checkbox.checked;
	const status = checkbox.closest('.main-card').querySelector('h6');
	if (boxstatus === false) {
		status.textContent = "不可使用";
	} else {
		status.textContent = "可使用";
	}
}

function addCoupon() {
	const couponbody = {
		'pcouponname': $("#pcouponname").val(),
		'productno': +$("#productno").val(),
		'pdiscount': +$("#pdiscount").val(),
		'preachprice': +$("#preachprice").val(),
		'pcoupnsdate': formatDate($("#pcoupnsdateup").val(), $("#pcoupnsdatedown").val()),
		'pcoupnedate': formatDate($("#pcoupnedateup").val(), $("#pcoupnedatedown").val())
	}
	swal({
		title: "是否新增優惠券?",
		icon: "warning",
		buttons: true,
		dangerMode: true
	}).then((confirm) => {
		if (confirm) {
			fetch('/pcoupon/add', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(couponbody)
			}).then(response => {
				if (response.ok) {
					swal('新增成功', { icon: "success" });
				} else {
					swal('新增失敗', { icon: "error" });
				}
			});
		} else {
			return Promise.reject('取消操作');
		}
	});
}


function formatDate(date, time) {
	const dateTime = new Date(`${date} ${time}`);
	const year = dateTime.getFullYear();
	const month = ('0' + (dateTime.getMonth() + 1)).slice(-2);
	const day = ('0' + dateTime.getDate()).slice(-2);
	const hours = ('0' + dateTime.getHours()).slice(-2);
	const minutes = ('0' + dateTime.getMinutes()).slice(-2);
	const seconds = ('0' + dateTime.getSeconds()).slice(-2);
	return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

function editCoupon(edit) {
	const couponno = edit;
	fetch(`/pcoupon/getbyno?id=${couponno}`, {
		method: 'GET',
	}).then(response => response.json())
		.then(data => {
			$('#editpcouponno').val(data[0].pcouponno);
			$('#editproductno').val(data[0].pfitcoupons && data[0].pfitcoupons[0] && data[0].pfitcoupons[0].pfitcouponNo.productno || "");
			$('#editpcouponname').val(data[0].pcouponname);
			$('#editpdiscount').val(data[0].pdiscount);
			$('#editpreachprice').val(data[0].preachprice);
			formatDate($("#editpcoupnsdateup").val(), $("#editpcoupnsdatedown").val());
			formatDate($("#editpcoupnedateup").val(), $("#editpcoupnedatedown").val());
		})
}

function editsaveCoupon() {
	const couponbody = {
		'pcouponno': $("#editpcouponno").val(),
		'pcouponname': $("#editpcouponname").val(),
		'productno': $("#editproductno").val(),
		'pdiscount': +$("#editpdiscount").val(),
		'preachprice': +$("#editpreachprice").val(),
		'pcoupnsdate': formatDate($("#editpcoupnsdateup").val(), $("#editpcoupnsdatedown").val()),
		'pcoupnedate': formatDate($("#editpcoupnedateup").val(),$("#editpcoupnedatedown").val())
	}
	swal({
		title: "是否更新優惠券?",
		icon: "warning",
		buttons: true,
		dangerMode: true
	}).then((confirm) => {
		if (confirm) {
			fetch('/pcoupon/edit', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(couponbody)
			}).then(response => {
				if (response.ok) {
					swal('更新成功', { icon: "success" });
				} else {
					swal('更新失敗', { icon: "error" });
				}
			});
		} else {
			return Promise.reject('取消操作');
		}
	});
}
function saveCoupon(couponno, event) {
		const btn = event.target;
		const couponNode = btn.closest(".coupon-active");
		const checkbox = couponNode.querySelector('input[type="checkbox"]');
		const checkboxValue = checkbox.checked;
		let statusbyte;
	if (checkboxValue === false) {
		statusbyte = 2;
	} else {
		statusbyte = 1;
	}
	const couponstatus = {
		'pcouponno': couponno,
		'pcouponstatus': statusbyte
	}
	swal({
		title: "是否更新優惠券狀態?",
		icon: "warning",
		buttons: true,
		dangerMode: true
	}).then((confirm) => {
		if (confirm) {
			fetch('/pcoupon/updateStatus', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(couponstatus)
			}).then(response => {
				if (response.ok) {
					swal('更新成功', { icon: "success" });
				} else {
					swal('更新失敗', { icon: "error" });
				}
			});
		} else {
			return Promise.reject('取消操作');
		}
	});
}