DESCRIPTION = "Kernel driver for NSS PPE core driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-ppe \
	   file://files \
	   "

PACKAGES += "kernel-module-qca-nss-ppe "

DEPENDS = "virtual/kernel qca-ssdk-nohnat nat46 qca-ovsmgr"
DEPENDS:ipq53xx_32_QRDK_256:remove = "nat46"
DEPENDS:ipq54xx_32_QRDK_256:remove = "nat46"

RDEPEND-{PN} = "qca-ssdk-nohnat"

S = "${WORKDIR}/qca-nss-ppe"
SSDK_STG_INCDIR = "${STAGING_INCDIR}/qca-ssdk"
NAT46_STG_INCDIR = "${STAGING_INCDIR}/nat46"

PPE_MAKE_OPTS:${SOC} += "ppe-drv=y \
			 PPE_IPSEC_ENABLE=y \
			 PPE_TUN_ENABLE=y \
			 "
PPE_MAKE_OPTS:ipq53xx_32_QRDK_256:remove = " PPE_IPSEC_ENABLE=y \
					     PPE_TUN_ENABLE=y \
					     "
PPE_MAKE_OPTS:ipq54x_32_QRDK_256:remove = " PPE_IPSEC_ENABLE=y \
					    PPE_TUN_ENABLE=y \
					    "

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-ssdk/init \
		-I${STAGING_INCDIR}/qca-ssdk/fal \
		-I${STAGING_INCDIR}/qca-ovsmgr \
		-I${STAGING_INCDIR}/ \
		"

MODULE_EXTRA_SYMBOLS ="${SSDK_STG_INCDIR}/Module.symvers ${NAT46_STG_INCDIR}/Module.symvers \
			${STAGING_INCDIR}/qca-ovsmgr/Module.symvers"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C  "${STAGING_KERNEL_BUILDDIR}"  ${PPE_MAKE_OPTS} \
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
	install -m 0644 ${S}/drv/ppe_drv/qca-nss-ppe${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-ppe
	install -d  ${STAGING_DIR}/usr/
	install -d  ${STAGING_DIR}/usr/include

	install -m 0644 ${S}/exports/* ${D}${includedir}/qca-nss-ppe/
	install -m 0644 ${S}/netlink/include/* ${D}${includedir}/qca-nss-ppe/
	install -m 0644 ${S}/drv/exports/* ${D}${includedir}/qca-nss-ppe/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe/Module.symvers
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/files/ppe_flow_dump ${D}${bindir}/ppe_flow_dump
	install -m 0755 ${WORKDIR}/files/ppe_if_map ${D}${bindir}/ppe_if_map
	install -m 0755 ${WORKDIR}/files/nss_perf_config.sh ${D}${bindir}/nss_perf_config
	cp ${TOPDIR}/../opensource/qca-nss-ppe/drv/exports/ppe_acl.h ${STAGING_DIR}/usr/include
	cp ${TOPDIR}/../opensource/qca-nss-ppe/drv/exports/ppe_drv_port.h ${STAGING_DIR}/usr/include
}

FILES:${PN} = "${bindir}/ppe_flow_dump \
	${bindir}/ppe_if_map \
	${bindir}/nss_perf_config \
	"

KERNEL_MODULE_AUTOLOAD:${PN} = " qca-nss-ppe"
