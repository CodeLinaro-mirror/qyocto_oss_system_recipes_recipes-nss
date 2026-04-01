DESCRIPTION = "Kernel driver for network function netfn sk offload"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}"

SRC_URI = " \
    file://qca-nss-netfn/ \
    file://files/ \
"

PACKAGES += "kernel-module-qca-nss-netfn-sk-offload"

DEPENDS = "virtual/kernel qca-nss-ppe qca-nss-ppe-vp qca-nss-flowmgr"

S = "${WORKDIR}/qca-nss-netfn/offload/sk_offload"

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-nss-ppe \
		-I${STAGING_INCDIR}/qca-nss-ppe-vp \
		-I${STAGING_INCDIR}/qca-nss-flowmgr \
		-I${STAGING_INCDIR}/qca-nss-netfn "

MODULE_EXTRA_SYMBOLS += " \
			${STAGING_INCDIR}/qca-nss-ppe/Module.symvers\
			${STAGING_INCDIR}/qca-nss-ppe-vp/Module.symvers \
			${STAGING_INCDIR}/qca-nss-flowmgr/Module.symvers"

do_compile() {
	unset LDFLAGS
	make -C  "${STAGING_KERNEL_BUILDDIR}" \
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
	install -m 0644 qca-nss-netfn-sk-offload${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-netfn-sk-offload
	install -m 0644 ${S}/../../exports/* ${D}${includedir}/qca-nss-netfn-sk-offload/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-netfn-sk-offload/Module.symvers
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/files/iperf3_load_balance.sh ${D}${bindir}/iperf3_load_balance.sh
}
FILES:${PN} =" \
	${bindir}/iperf3_load_balance.sh \
"
