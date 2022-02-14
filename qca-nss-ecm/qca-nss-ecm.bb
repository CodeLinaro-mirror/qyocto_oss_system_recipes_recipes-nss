DESCRIPTION = "NSS ECM (Enhanced Connection Manager)"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
inherit systemd

SOC_TYPE="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-ecm \
	   file://files \
	   "

DEPENDS_append += "virtual/kernel "
DEPENDS_append_ipq40xx = "simulated-driver"
DEPENDS_append_ipq807x = "qca-nss-drv"
DEPENDS_append_ipq807x-64 = "qca-nss-drv"
DEPENDS_ipq95xx += "qca-nss-sfe"
DEPENDS_ipq95xx_64 += "qca-nss-sfe"

RDEPENDS-${PN}_append += "iptables-mod-extra ipt-conntrack \
		ipv6 l2tp pppol2tp bonding pptp \
		pppoe nat46 "
RDEPENDS-${PN}_append_ipq40xx = "simulated-driver"
RDEPENDS-${PN}_append_ipq807x = "qca-nss-drv"
RDEPENDS-${PN}_append_ipq807x-64 = "qca-nss-drv"

S = "${WORKDIR}/qca-nss-ecm"
SFE_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-sfe"

PACKAGES += "kernel-module-ecm"
INSANE_SKIP_${PN} = "dev"

FRONT_END_NSS_ENABLE = "n"
FRONT_END_NSS_ENABLE_append_ipq40xx = "n"
FRONT_END_NSS_ENABLE_append_ipq807x = "y"
FRONT_END_NSS_ENABLE_append_ipq807x-64 = "y"

export ECM_FRONT_END_NSS_ENABLE="${FRONT_END_NSS_ENABLE}"

ECM_MAKE_OPTS_append += "ECM_IPV6_ENABLE=y "
ECM_MAKE_OPTS_ipq95xx += " ECM_FRONT_END_SFE_ENABLE=y"
ECM_MAKE_OPTS_ipq95xx_64 += "ECM_FRONT_END_SFE_ENABLE=y"

EXTRA_CFLAGS += "-I${STAGING_INCDIR}/qca-nss-sfe"

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH='${KARCH}' \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		KBUILD_EXTRA_SYMBOLS="${SFE_STG_INCDIR}/Module.symvers" \
		SoC='${SOC_TYPE}' \
		${ECM_MAKE_OPTS} \
		modules
}
do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ecm${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}/usr/bin
	install -m 0755 ${WORKDIR}/files/ecm_dump.sh ${D}${bindir}/ecm_dump.sh
	install -m 0755 ${WORKDIR}/files/qca-nss-ecm ${D}${bindir}/qca-nss-ecm
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/files/qca-nss-ecm.service ${D}${systemd_unitdir}/system/qca-nss-ecm.service
	install -d ${D}${sysconfdir}/sysctl.d
	install -m 0644 ${WORKDIR}/files/qca-nss-ecm.sysctl ${D}${sysconfdir}/sysctl.d/99-qca-nss-ecm.conf
	install -d ${D}${includedir}/qca-nss-ecm
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ecm/Module.symvers
}

SYSTEMD_SERVICE_${PN} += "qca-nss-ecm.service"

FILES_${PN} = " \
	${systemd_unitdir}/system/qca-nss-ecm.service \
	${bindir}/qca-nss-ecm \
	${bindir}/ecm_dump.sh \
	${sysconfdir}/sysctl.d/99-qca-nss-ecm.conf \
	"
