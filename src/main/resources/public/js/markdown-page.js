const app = new Vue({

    el: "#markdown-app",
    delimiters: ['((', '))'],
    data: {
        focus: false,

        currentPage: null,
        currentPageCopy: null,

        newPage: {
            title: "",
            slug: "",
            content: "",
            type: "",
            live: false
        },
        syncing: false,
        pages: [],
        pageTypes: []
    },
    computed: {
        compiledMarkdown: function () {
            if (this.currentPage == null) {
                return "";
            }
            return marked(this.currentPage.content, {sanitize: true});
        },
        noChanges: function () {

            return JSON.stringify(this.currentPage) === JSON.stringify(this.currentPageCopy)
        }
    },
    methods: {
        getPageTypes: function () {
            let selfReference = this;
            axios({
                method: 'get',
                url: '/page-types',
            })
                .then(function (response) {
                    selfReference.pageTypes = response.data
                })
                .catch(function (error) {
                    UIkit.notification(error.response.data, {status: 'danger'})

                });
        },
        update: _.debounce(function (e) {
            this.input = e.target.value
        }, 300),

        getPages: function () {

            let selfReference = this;
            axios({
                method: 'get',
                url: '/api/pages',
            })
                .then(function (response) {
                    selfReference.pages = response.data.values;

                    if (selfReference.currentPage != null) {
                        selfReference.selectById(selfReference.currentPage.id)
                    }
                })
                .catch(function (error) {

                    UIkit.notification(error.response.data.message, {status: 'danger'})

                });

        },
        syncPage: function (currentPage) {

            this.syncing = true;
            let selfReference = this;
            axios({
                method: 'get',
                url: '/api/sync-page/' + currentPage.id,
            })
                .then(function (response) {

                    UIkit.notification(response.data.message, {status: 'success'})
                    selfReference.syncing = false;
                    selfReference.getPages()
                })
                .catch(function (error) {

                    selfReference.syncing = false;
                    UIkit.notification(error.response.data, {status: 'danger'})

                });
        },
        selectById: function (id) {
            this.selectPage(this.pages.find(x => x.id === id));
        },
        selectPage: function (page) {
            this.currentPage = JSON.parse(JSON.stringify(page));
            this.currentPageCopy = JSON.parse(JSON.stringify(page));
        },
        newPageOpen: function () {
            UIkit.notification.closeAll();
            this.newPage = {
                title: "",
                slug: "",
                content: "# New Page\n",
                type: this.pageTypes[0].value,
                live: false
            }
            ;
            UIkit.modal(document.getElementById("new-page-modal")).show();
        },
        newPageSave: function () {
            let selfReference = this;
            UIkit.notification.closeAll();
            axios({
                method: 'post',
                url: '/api/pages',
                data: selfReference.newPage
            })
                .then(function (response) {

                    UIkit.notification("Successfully created the page, click on '" + selfReference.newPage.title + "' in the menu to edit it. ", {status: 'success'})
                    selfReference.getPages()
                    UIkit.modal(document.getElementById("new-page-modal")).hide();
                })
                .catch(function (error) {
                    console.log(error);
                    UIkit.notification(error.response.data, {status: 'danger'})

                });
        },
        editPageSave: function () {
            let selfReference = this;
            axios({
                method: 'put',
                url: '/api/pages/' + selfReference.currentPage.id,
                data: selfReference.currentPage
            })
                .then(function (response) {
                    selfReference.getPages();
                    UIkit.notification.closeAll();
                    UIkit.notification("Successfully updated the page!", {status: 'success'})
                })
                .catch(function (error) {
                    UIkit.notification(error.response.data, {status: 'danger'})

                });
        },
        deletePage: function () {
            var ref = this;
            UIkit.modal.confirm('Are you sure that you want to delete this page?', {
                labels: {
                    ok: "Yes",
                    cancel: "Cancel"
                }, stack: true
            }).then(function () {
                axios({
                    method: 'delete',
                    url: '/api/pages/' + ref.currentPage.id
                }).then(function (response) {
                    UIkit.notification('Successfully deleted the page', {status: 'success'});
                    ref.currentPage = null;
                    ref.getPages()
                })
                    .catch(function (error) {
                        UIkit.notification(error.response.data.message, {status: 'danger'})
                    });
            }, function () {

            });
        }


    },
    created: function () {

        this.getPageTypes();
        this.getPages();
    }

});