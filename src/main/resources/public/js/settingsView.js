const app = new Vue({
    el: '#app',
    delimiters: ['((', '))'],
    components: {
        'vuejs-datepicker': vuejsDatepicker,
        'vuejs-colorpicker': VueColor.Chrome
    },
    data: {
        bufData: {},
        newData: {},
        selectedData: null,
        selectedModel: null,
        models: [],
        settings: [],
        searchTimeout: null,
        searchQuery: "",
        lastSelectedPageNum: 1,
        curPage: {
            currentPageNumber: 1
        },
        gallery: [],
        selectedGalleryImage: {link:""},
        selectingImage: null,
        cachedSettings: ''

    },
    computed: {

        lastPageNumber: function () {
            return this.curPage.lastPageNumber;
        }
    },

    methods: {
        selectImage: function(){

            this.getById( this.selectingImage ).value = '/images/'+this.selectedGalleryImage.id;
            UIkit.modal(document.getElementById('gallery-modal')).show()
        },
        selectGallery: function(id){
            this.selectedGalleryImage = {link:""};
            this.selectingImage = id;

            UIkit.modal(document.getElementById('gallery-modal')).show()

        },
        saveSettings: function () {
            var ref = this;
            axios({
                method: 'put',
                url: '/settings',
                data: ref.settings
            }).then(function (response) {

                UIkit.notification("Successfully updated settings!", {status: 'success', pos: 'bottom-center'})

                ref.getSettings();
            })
                .catch(function (error) {
                    UIkit.notification(error.response.data, {status: 'danger'})

                });

        },
        getGallery: function () {
            var ref = this;
            axios({
                method: 'get',
                url: '/images/gallery',
                data: ref.settings
            }).then(function (response) {


                ref.gallery = response.data;
            })
                .catch(function (error) {
                    UIkit.notification(error.response.data, {status: 'danger'})

                });
        },
        getById: function (id) {

            var data = this.settings;
            for (var i = 0; i < data.length; i++) {

                if (data[i].id === id) {
                    return (data[i]);
                }
            }
            return {description: "", defaultValue: "", value: ""};
        },
        defaultValue: function (setting) {

            var defVal = $.trim(setting.default_value);


            if (defVal.match("#")) {
                return setting;
            }
            var defSetting = this.getById(defVal)
            var val = defSetting.value;
            if (val.match("#")) {
                return {default_value: val};
            }
            return this.defaultValue(defSetting)
        },
        getColorValue: function (id) {

            var obj = this.getById(id);
            if (obj.value === "") {

                var defVal = this.defaultValue(obj);
                return defVal.default_value;
            } else {
                return obj.value;
            }
        },
        getSettings: function () {
            var ref = this;
            axios.get("/settings")
                .then(function (response) {


                    ref.settings = response.data.values;
                    ref.cachedSettings = JSON.stringify(response.data.values);
                })
                .catch(function (error) {
                    UIkit.notification(error.response.status, {status: 'danger'})
                });
        }
    },
    created: function () {

        this.getGallery();
        this.getSettings();

    }
});

function startsWith(str, word) {
    return str.lastIndexOf(word, 0) === 0;
}