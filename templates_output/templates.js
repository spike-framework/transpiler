spike.core.Assembler.sourcePath = 'templates_input';
spike.core.Templates.templates['templates_input_new_test_html'] = function (scope) {
  var t = '';
  t += '';
  t += '<div class=\"filterElement\" sp-watch=\"watcher-0\" sp-id=\"watcher-0\">';
  t += '<div class=\"view-types col-xs-12\">';
  t += '<div class=\"template-name\">';
  t += 'Template name';
  t += '<\/div>';
  t += '<select class="form-control" spike-unbinded="" spike-event-change="scope.parentElement.VIEW_TYPE=event.target.value;scope.changeView()" spike-event-change-link="' + (spike.core.Events.linkEvent(0, function (event) {
      scope.parentElement.VIEW_TYPE = event.target.value;
      scope.changeView()
    })) + '">';
  for (var argumentKey in scope.parentElement.VIEW_TYPES) {
    if (scope.parentElement.VIEW_TYPES.hasOwnProperty(argumentKey)) {
      var argument = scope.parentElement.VIEW_TYPES[argumentKey];
      (function (argument, argumentKey) {
        t += '<option sp-value="' + (argumentKey) + '" ' + (((scope.parentElement.VIEW_TYPE === argument) ? 'selected' : '')) + '>' + (argument) + '</option>';
      }(argument, argumentKey));
    }
  }
  t += '</select>';
  t += '<\/div>';
  t += '<input type=\"text\" name=\"loginName\" placeholder=\"' + ( Message.get('login_placeholder') ) + '\" spike-event-keyup=\"scope.model.loginName=event.target.value;\" spike-event-keyup-link=\"' + (spike.core.Events.linkEvent(1, function (event) {
      scope.model.loginName = event.target.value;
    })) + '\" spike-unbinded=\"\" sp-name=\"loginName\">';
  t += '<input type=\"password\" name=\"password\" placeholder=\"' + ( Message.get('password_placeholder') ) + '\" spike-event-keyup=\"scope.model.password=event.target.value;\" spike-event-keyup-link=\"' + (spike.core.Events.linkEvent(2, function (event) {
      scope.model.password = event.target.value;
    })) + '\" spike-unbinded=\"\" sp-name=\"password\">';
  t += '<\/div>';
  return t;
};