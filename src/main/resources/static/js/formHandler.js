function isListArray(arr) {
    return Array.isArray(arr); // Simply check if it's an array
}
function isKeyValueObject(data) {
    return typeof data === 'object' && data !== null && !Array.isArray(data);
}
const containsSquareBrackets = (str) => {
    const pattern = /\[.*\]/;
    return pattern.test(str);
};
var addFormData = (v, d) => {
    var val = $(d).val();
    switch (d.type) {
        case 'number':
        case 'range':
            val = parseFloat(val); // Parse as float if needed
            break;
        case 'checkbox':
            val = $(d).is(":checked") ? 1 : 0; // Convert to 1 or 0
            break;
        case 'file':
            v[d.name] = $(d)[0].files[0]; // Store the file object
            return; // Exit early for file inputs
        default:
            // For other types, handle accordingly
            val = $(d).hasClass('input-options') ? val.split('|') : val;
            // Check if val can be converted to number
            if (!isNaN(val)) {
                val = parseFloat(val); // Parse as float if needed
            }
    }

    if (v[d.name] != undefined) {
        if (typeof v[d.name] == 'string' || typeof v[d.name] == 'number') {
            v[d.name] = [
                v[d.name], val
            ];
        } else {
            v[d.name].push(val);
        }
    } else {
        if (d.type === 'file') {
            v[d.name] = $(d)[0].files[0]; // Store the file object
        } else {
            v[d.name] = val;
        }
    }
    // console.log($(d), v[d.name], d.name,d.type);
};

function getFormData(id, ref, values = {}, include_nulls = false) {
    var inputs = $('#' + id + ' :input'),
        tect_editors = $('#' + id + ' .mini-text-editor-area,#' + id + ' .text-editor-area ,#' + id + ' .simple-editor-area'),
        key = $('#' + id).attr('data-cf-formkey'),
        sub_forms = $('#' + id).find('.sub-form');
    tect_editors.each(function (e) {
        var editor_id = $(this).attr('id'), content = tinymce.get(editor_id).getContent();
        if ($(this).hasClass('skip-input')) {
            return;
        }
        var skip = $(this).parents('.skip-form').first();

        if (skip.length != 0) {
            if (skip.attr('id') != id && skip.find('#' + id).length == 0) {
                return;
            }
        }
        tinymce.get(editor_id).save();
        var name = $(this).attr('name');
        if (include_nulls || (content != '')) {
            values[(name == undefined || name == null) ? editor_id : name] = content;
        }
    });

    sub_forms = sub_forms.filter(function () {
        var sub = $(this), parent = sub.parents('.sub-form').first();
        if ($('#' + id).find(parent).length != 0) {
            return false;
        }
        return true;
    });
    inputs = inputs.filter(function () {
        var input = $(this), found = false; parent = input.parents('.sub-form').first();
        if ($('#' + id).find(parent).length != 0) {
            if ($('#' + id) != parent[0] && parent.length != 0) {
                found = true;
            }
        }
        return !found;
    });
    sub_forms.each(function () {
        if ($(this).attr('data-cf-setunder') != null) {
            if (values[$(this).attr('data-cf-setunder')] == undefined) {
                values[$(this).attr('data-cf-setunder')] = {};
            }
            values[$(this).attr('data-cf-setunder')][$(this).attr('id')] = getFormData($(this).attr('id'), ref, values[$(this).attr('id')], include_nulls);
        } else {
            values[$(this).attr('id')] = getFormData($(this).attr('id'), ref, values[$(this).attr('id')], include_nulls);
        }

    });
    inputs.each(function () {
        if (this.type == 'submit') {
            return;
        }

        if (this.type == 'hidden' || $(this).hasClass('tox-tbtn') || $(this).tagName() == "BUTTON" ||
            ($(this).parents('.dropdown.bootstrap-select').length > 0 && $(this).tagName() != "SELECT")) {
            return
        }
        if ($(this).hasClass('skip-input')) {
            return;
        }
        var skip = $(this).parents('.skip-form').first();

        // var sub = $(this).parents('.sub-form').first();
        var list = $(this).parents('.form-list-items').first();

        if (skip.length != 0) {

            if (skip.attr('id') != id && skip.find('#' + id).length == 0) { //chcks if the skipform is not a parent of current form
                return;
            }
        }
        // if (sub.length != 0) {
        //     if (sub.attr('id') != id && sub.find('#' + id).length == 0) {
        //         var subs = $(this).parents('.sub-form');
        //         // values[sub.attr('id')] = getFormData(sub.attr('id'));
        //         return;
        //     }
        // }
        if (list.length != 0 && list.attr('id') != ref) {
            var list_item = $(this).parents(".form-list-item").first();
            // console.log(list.find(".form-list-item").length);
            if (list_item.length != 0) {
                if (values[list.attr('id')] == undefined) {
                    values[list.attr('id')] = [];
                } else {
                    var i = 0;
                    $.each(list.find(".form-list-item"), (k, v) => {
                        if ($(v).attr('id') == list_item.attr('id')) {
                            i = k;
                        }
                    })
                    if (values[list.attr('id')].length - 1 == i) {
                        return;
                    }
                }
                // if (list_item.attr('id') != id && list_item.find('#' + id).length == 0) {
                values[list.attr('id')].push(getFormData(list_item.attr('id'), list.attr('id'), {}, include_nulls))
                // }
            }
            return;
        }

        if ($(this).hasClass('key')) {

            return
        }
        if ($(this).attr('data-cf-setunder') != null) {
            if ($(this).val() != '') {
                if (values[$(this).attr('data-cf-setunder')] == undefined) {
                    values[$(this).attr('data-cf-setunder')] = {};
                }
                if (this.type === 'file') {
                    addFormData(values[$(this).attr('data-cf-setunder')], this);
                } else if (this.type == 'checkbox') {
                    addFormData(values[$(this).attr('data-cf-setunder')], this);
                }
                else if (this.type == 'radio') {
                    if (!$(this).is(":checked")) {
                        return
                    }
                    // if (values[$(this).attr('data-cf-setunder')][this.name] == undefined || values[$(this).attr('data-cf-setunder')][this.name] == 0) {
                    //     values[$(this).attr('data-cf-setunder')][this.name] = undefined
                    addFormData(values[$(this).attr('data-cf-setunder')], this);
                    // }
                }
                else if ($(this).tagName() == 'SELECT') {
                    if (include_nulls) {
                        addFormData(values[$(this).attr('data-cf-setunder')], this);
                    } else if ($(this).val() != -1) {
                        addFormData(values[$(this).attr('data-cf-setunder')], this);
                    }
                } else {
                    addFormData(values[$(this).attr('data-cf-setunder')], this);
                }
            }
        }
        else if (($(this).val() != '' && !include_nulls) || include_nulls) {

            if ($(this).hasClass('value')) {
                var key = $(this).parents('.key_value').first().find('.key').first().val();
                values[key] = $(this).val();
                return;
            }
            if (this.type === 'file') {
                addFormData(values, this);
            } else if (this.type == 'checkbox') {
                addFormData(values, this);
            } else if (this.type == 'radio') {
                if (!$(this).is(":checked")) {
                    return
                }
                // if (values[this.name] == undefined || values[this.name] == 0) {
                //     values[this.name] = undefined
                addFormData(values, this);
                // }
            } else if ($(this).tagName() == 'SELECT') {
                if (include_nulls) {
                    addFormData(values, this);
                }
                else if ($(this).val() != -1) {
                    addFormData(values, this);
                }
            } else {
                addFormData(values, this);
            }
        }

    });

    if (key != undefined) {
        var v = {};
        v[key] = values;
        return v;
    }
    return values;
}

jQuery.fn.tagName = function () {
    return this.prop("tagName");
};

function convertToFormData(values) {
    function appendFormData(formData, key, value) {
        if (value instanceof File) {
            formData.append(key, value, value.name);
        } else if (value instanceof FileList) {
            for (var i = 0; i < value.length; i++) {
                formData.append(key + '_' + i, value[i], value[i].name);
            }
        } else {
            if (isKeyValueObject(value) || isListArray(value)) {
                formData.append(key, JSON.stringify(value));
            } else {
                formData.append(key, value);

            }
        }
    }

    var formData = new FormData();
    Object.keys(values).forEach(function (key) {
        appendFormData(formData, key, values[key]);
    });
    return formData;
}