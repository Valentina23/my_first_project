$(document).ready(function () {
    var uploaderBtn = $('#upload');
    var classes = uploaderBtn.attr('class').split(" ");
    for (x in classes) {
        if (classes[x] == 'inAction') {
            uploaderBtn.removeClass('inAction')
            $('#superBtn')[0].click();
        }
    }
})