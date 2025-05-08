DESCRIPTION = "Kernel driver for PPE test module"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

SOC = "${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-ppe/"

PACKAGES += "kernel-module-qca-nss-ppe-mirror-test"

DEPENDS = "virtual/kernel qca-nss-ppe-rule"

S = "${WORKDIR}/qca-nss-ppe/test"

EXTRA_CFLAGS += "-I${STAGING_INCDIR}/qca-ssdk \
                -I${STAGING_INCDIR}/qca-ssdk/fal"

MODULE_EXTRA_SYMBOLS = "${STAGING_INCDIR}/qca-nss-ppe-rule/Module.symvers "

do_compile() {
	unset LDFLAGS
	make -C ${STAGING_KERNEL_BUILDDIR} \
	CROSS_COMPILE="${TARGET_PREFIX}" \
	ARCH="${KARCH}" \
	M="${S}" \
	EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
	KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
	SoC='${SOC_TYPE}' \
	modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/qca-nss-ppe-mirror-test${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-ppe-test
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe-test/Module.symvers
}
