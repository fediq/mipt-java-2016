/**
 * Created by ilya on 17.12.16.
 */
var baseUrl = 'http://localhost:8080';

$(document).ready(function () {
    updateLists();
});

var updateLists = function () {
    updateFunctionList();
    updateVariablesList();
};

var updateFunctionList = function () {
    $.ajax({
        type: "GET",
        url: baseUrl + '/function',
        success: function (data) {
            console.log(data);
            var curTable = $("#functionsList")[0];
            curTable.innerHTML = "";
            for (var i = 0; i < data.length; i++) {
                var newRow = curTable.insertRow(curTable.rows.length);
                var numberCell = newRow.insertCell(newRow.cells.length);
                numberCell.innerText = (i + 1).toString();
                var dataCell = newRow.insertCell(newRow.cells.length);
                dataCell.innerText = data[i];
            }
        },
        error: function (data) {
            console.log(data.responseText);
        }
    })
};

var getVariableValue = function (name, cell) {
    cell.innerText = 'undefined';
    $.ajax({
        type: "GET",
        url: baseUrl + '/variable/' + name,
        success: function (data) {
            console.log(data);
            cell.innerText = data;
        },
        error: function (data) {
            console.log(data);
            cell.innerText = 'fail to load value';
        }
    });
};

var updateVariablesList = function () {
    $.ajax({
        type: "GET",
        url: baseUrl + '/variable',
        success: function (data) {
            console.log(data);
            var curTable = $("#variablesList")[0];
            curTable.innerHTML = "";
            for (var i = 0; i < data.length; i++) {
                var newRow = curTable.insertRow(curTable.rows.length);
                var numberCell = newRow.insertCell(newRow.cells.length);
                numberCell.innerText = (i + 1).toString();
                newRow.insertCell(newRow.cells.length).innerText = data[i];
                getVariableValue(data[i], newRow.insertCell(newRow.cells.length));
            }
        },
        error: function (data) {
            console.log(data);
            $('#result2')[0].textContent = data.toString();
        }
    });
};

var getFunction = function () {
    updateLists();
    $.ajax({
        type: 'GET',
        url: baseUrl + '/function/' + $("#get_function_input")[0].value,
        contentType: "text/plain",
        success: function (data) {
            console.log(data);
            $("#result_get_function_name")[0].textContent = 'Function name : ' + $("#get_function_input")[0].value;
            $("#result_get_function_body")[0].textContent = data.body;
            var argsTable = $("#result_get_function_args")[0];
            var cnt = 0, cellsInOneRow = 4;
            argsTable.innerHTML = "";

            while (cnt < data.args.length) {
                var newRow = argsTable.insertRow(argsTable.rows.length);
                for (var i = 0; i < cellsInOneRow; i++) {
                    var newCell = newRow.insertCell(i);
                    if (cnt < data.args.length) {
                        newCell.innerText = (cnt + 1) + " : " + data.args[i];
                        cnt += 1;
                    }
                }
            }
        },
        error: function (data) {
            console.log(data);
            $('#result_get_function_body')[0].textContent = data.responseText;
        }
    });
};

var submitEval = function () {
    updateLists();
    $.ajax({
        type: "POST",
        url: baseUrl + '/eval',
        contentType: "text/plain",
        data: $("#eval_input")[0].value,
        success: function (data) {
            console.log(data);
            $("#result_eval")[0].textContent = data.toString();
            updateFunctionList();
        },
        error: function (data) {
            console.log(data);
            $('#result_eval')[0].textContent = data.responseText;
        }
    });
};

var addFunction = function () {
    var urlHeader = $("#add_function_input_name")[0].value + '?';
    var params = $("#add_function_input_params")[0].value.split(',');
    for (var i = 0; i < params.length; i++) {
        if (i != 0) {
            urlHeader += '&';
        }
        urlHeader += 'args=' + params[i].trim();
    }
    $.ajax({
        type: "PUT",
        url: baseUrl + '/function/' + urlHeader,
        contentType: "text/plain",
        data: $("#add_function_input_body")[0].value,
        success: function (data) {
            console.log(data);
            $("#result_add_function")[0].textContent = data.toString();
            updateFunctionList();
        },
        error: function (data) {
            console.log(data);
            $('#result_add_function')[0].textContent = data.responseText;
        }
    });
};

var deleteFunction = function () {
    $.ajax({
        type: "DELETE",
        url: baseUrl + '/function/' + $("#delete_function_input_name")[0].value,
        contentType: "text/plain",
        success: function (data) {
            console.log(data);
            $("#result_delete_function")[0].textContent = data.toString();
            updateFunctionList();
        },
        error: function (data) {
            console.log(data);
            $('#result_delete_function')[0].textContent = data.responseText;
        }
    });
};

var addVariable = function () {
    $.ajax({
        type: "PUT",
        url: baseUrl + '/variable/' + $("#add_variable_input_name")[0].value,
        contentType: "text/plain",
        data: $("#add_variable_input_body")[0].value,
        success: function (data) {
            console.log(data);
            $("#result_add_variable")[0].textContent = data.toString();
            updateVariablesList();
        },
        error: function (data) {
            console.log(data);
            $('#result_add_variable')[0].textContent = data.responseText;
        }
    });
};

var deleteVariable = function () {
    $.ajax({
        type: "DELETE",
        url: baseUrl + '/variable/' + $("#delete_variable_input_name")[0].value,
        contentType: "text/plain",
        success: function (data) {
            console.log(data);
            $("#result_delete_variable")[0].textContent = data.toString();
            updateVariablesList();
        },
        error: function (data) {
            console.log(data);
            $('#result_delete_variable')[0].textContent = data.responseText;
        }
    });
};