DESCRIPTION = "ECM WIFI Plugin"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-ecm "
PACKAGES += "kernel-module-qca-nss-ecm-wifi-plugin "

DEPENDS = "virtual/kernel qca-nss-ecm qca-wifi"

ECM_MAKE_OPTS:${SOC} += "ECM_CLASSIFIER_MSCS_SCS_ENABLE=y \
			ECM_CLASSIFIER_WIFI_ENABLE=y \
			"

S = "${WORKDIR}/qca-nss-ecm/ecm_wifi_plugins"

EXTRA_CFLAGS += "-I${STAGING_INCDIR}/qca-wifi \
		 -I${STAGING_INCDIR}/qca-nss-ecm "

MODULE_EXTRA_SYMBOLS =" ${STAGING_INCDIR}/qca-wifi/Module.symvers \
			${STAGING_INCDIR}/qca-nss-ecm/Module.symvers "

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
		SoC='${SOC_TYPE}' \
		${ECM_MAKE_OPTS} \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/ecm-wifi-plugin${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}

