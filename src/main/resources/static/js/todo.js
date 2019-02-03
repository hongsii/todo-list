$(document).ready(function() {
  bindEvent();
  loadTaskList();
});

function bindEvent() {
  $('.modal').on('hidden.bs.modal', function (e) {
    $(this)
    .find("input,textarea,select").val('').end()
    .find("input[type=checkbox], input[type=radio]").prop("checked", "").end();
  });

  $('#update-modal').on('show.bs.modal', function (event) {
    var taskId = $(event.relatedTarget).attr('data-task-id');
    $.ajax({
      url: 'api/tasks/' + taskId,
      type: 'GET',
      success: function (response) {
        const task = response.data.task;
        $('#update-modal .modal-body input[name=id]').val(task.id);
        $('#update-modal .modal-body input[name=content]').val(task.content);
        $('#update-modal .modal-body input[name=super-task-ids]').val(task.superTaskIds);
      }
    });
  })

  $('#saveTaskBtn').click(function() {
    var task = {
      content: $('#popup-modal .modal-body input[name=content]').val(),
      superTaskIds: parseSuperTaskIds($('#popup-modal .modal-body input[name=super-task-ids]').val())
    };

    $.ajax({
      url: '/api/tasks',
      type: 'POST',
      data: JSON.stringify(task),
      dataType: 'json',
      contentType: "application/json; charset=utf-8",
      success: function (response) {
        console.log(response);
        $('#pagination li.active').trigger('click');
        $('#popup-modal').modal('hide');
        showAlertPopup('저장되었습니다.');
      },
      error: function (response) {
        $('#popup-modal').modal('hide');
        showAlertPopup(response.responseJSON.message, ALERT_TYPE.ERROR, 2000);
      }
    });
  });

  $('#updateBtn').click(function() {
    var task = {
      id: $('#update-modal .modal-body input[name=id]').val(),
      content: $('#update-modal .modal-body input[name=content]').val(),
      superTaskIds: parseSuperTaskIds($('#update-modal .modal-body input[name=super-task-ids]').val()),
    };

    $.ajax({
      url: '/api/tasks/' + task.id,
      type: 'PUT',
      data: JSON.stringify(task),
      dataType: 'json',
      contentType: "application/json; charset=utf-8",
      success: function (response) {
        console.log(response);
        $('#pagination li.active').trigger('click');
        $('#update-modal').modal('hide');
        showAlertPopup('저장되었습니다.');
      },
      error: function (response) {
        $('#update-modal').modal('hide');
        showAlertPopup(response.responseJSON.message, ALERT_TYPE.ERROR, 2000);
      }
    });
  });

  $('#task-list-size').change(function() {
    loadTaskList();
  })
}

function parseSuperTaskIds(ids) {
  return ids ? ids.split(","):[];
}

function loadTaskList(url) {
  // 페이지는 0부터 시작
  $.ajax({
    url: url || makeTaskListURL(),
    type: 'GET',
    dataType: 'json',
    success: function (response) {
      var tasks = response.data.tasks;
      console.group("TASK-RESPONSE");
      console.log(tasks);
      $("#task-list tbody").empty();
      for (index in tasks.content) {
        const task = tasks.content[index];
        console.log(task);

        const completeBtn = $('<button></button>').addClass('btn').attr('data-completed', task.completed).click(function() {
          completeTask(task.id);
        });
        if (task.completed) {
          completeBtn.text('완료').addClass('btn-secondary').prop("disabled", true);
        } else {
          completeBtn.text('진행').addClass('btn-danger');
        }

        $('<tr/>').attr('id', 'row' + task.id)
          .append($('<td/>').text(task.id))
          .append($('<td/>').append($('<span style="cursor:pointer;color:#007bff" data-toggle="modal" data-target="#update-modal">').attr('data-task-id', task.id).attr('data-content', task.content).attr('data-super-task-ids', task.superTaskIds).text(task.content + makeSuperTaskIds(task.superTaskIds))))
          .append($('<td/>').text(task.createdDate))
          .append($('<td/>').text(task.modifiedDate))
          .append($('<td/>').append(completeBtn))
          .appendTo($("#task-list tbody"));
      }
      console.groupEnd();
      pagination('#pagination', tasks)
    },
    error: function (response) {
      console.log(response);
    }
  });
}

function makeSuperTaskIds(superTaskIds) {
  let tag = [];
  for (let index = 0; index < superTaskIds.length; index++) {
    tag.push('@' + superTaskIds[index]);
  }
  return tag.length > 0 ? ' ' + tag.join(' '):'';
}

function pagination(paginationId, pagingData) {
  $(paginationId).empty();

  for (let pageNumber = 0; pageNumber < pagingData.totalPages; pageNumber++) {
    var li = $('<li/>').addClass('page-item ' + ((pageNumber == pagingData.number) ? 'active' : ''))
      .click(function () {
        loadTaskList(makeTaskListURL(pageNumber));
      })
      .append($('<span/>').text(pageNumber + 1).addClass('page-link'));
    $(paginationId).append(li);
  }

  $(paginationId).prepend(
      $('<li class="page-item prev"><span class="page-link" href="javascript:void(0)" aria-label="Previous"> <span aria-hidden="true">&laquo;</span> </span> </li>')
  );

  $(paginationId).append(
      $('<li class="page-item next"><span class="page-link" aria-label="Next"><span aria-hidden="true">&raquo;</span></span></li>')
  );

  var pageItem = $(paginationId + " li").not(".prev, .next");
  var prev = $(paginationId + " li.prev");
  var next = $(paginationId + " li.next");

  pageItem.click(function() {
    pageItem.removeClass("active");
    $(this).not(".prev,.next").addClass("active");
  });

  next.click(function() {
    var currentPage = $('li.active');
    var nextPage = currentPage.next();
    if (nextPage.hasClass('next')) return;

    currentPage.removeClass('active');
    nextPage.addClass('active').trigger('click');
  });

  prev.click(function() {
    var currentPage = $('li.active');
    var prevPage = currentPage.prev();
    if (prevPage.hasClass('prev')) return;

    currentPage.removeClass('active');
    prevPage.addClass('active').trigger('click');
  });
}

function makeTaskListURL(requestPageNumber) {
  var size = $('#task-list-size').val();
  var page = requestPageNumber || 0;
  return '/api/tasks?size=' + size + '&page=' + page;
}

function completeTask(taskId) {
  $.ajax({
    url: '/api/tasks/' + taskId + '/complete',
    type: 'PATCH',
    dataType: 'json',
    success: function(response) {
      showAlertPopup("완료되었습니다 !");
      $('#pagination li.active').trigger('click');
    },
    error: function (response) {
      showAlertPopup(response.responseJSON.message, ALERT_TYPE.ERROR);
    }
  });
}

function showAlertPopup(message, alertType, time) {
  alertType = alertType || ALERT_TYPE.SUCCESS;
  time = time || alertType.time;
  var alertPopup = $('<div class="alert hide ' + alertType.class +'"  role="alert" id="alert-popup"></div>');
  $(alertPopup).appendTo($('body'))
  $(alertPopup)
  .append($('<strong/>').text(message))
  .fadeTo(time, 500).slideUp(300, function(){
    $(alertPopup).slideUp(300);
    $(alertPopup).remove();
  });
}

const ALERT_TYPE = {
  SUCCESS: { class : 'alert-success', time : 1200 },
  ERROR: { class : 'alert-danger', time : 2000 }
};