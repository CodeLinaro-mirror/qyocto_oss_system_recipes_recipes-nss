DESCRIPTION = "NSS ECM (Enhanced Connection Manager)"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
inherit systemd

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-ecm \
	   file://files \
	   "

DEPENDS_append += "virtual/kernel "
DEPENDS_append_ipq40xx = "simulated-driver"
DEPENDS_append_ipq807x-64 = "qca-nss-drv"
DEPENDS_append_ipq807x = "qca-nss-drv"
DEPENDS_ipq95xx_64 += "qca-nss-sfe nat46 qca-mcs-lkm qca-nss-ppe "
DEPENDS_ipq95xx += "qca-nss-sfe nat46 qca-mcs-lkm qca-nss-ppe "

RDEPENDS-${PN}_append += "iptables-mod-extra ipt-conntrack \
		ipv6 l2tp pppol2tp bonding pptp \
		pppoe nat46 "
RDEPENDS-${PN}_append_ipq40xx = "simulated-driver"
RDEPENDS-${PN}_append_ipq807x-64 = "qca-nss-drv"
RDEPENDS-${PN}_append_ipq807x = "qca-nss-drv"

S = "${WORKDIR}/qca-nss-ecm"
SFE_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-sfe"
NAT46_STG_INCDIR = "${STAGING_INCDIR}/nat46"
MCS_STG_INCDIR = "${STAGING_INCDIR}/qca-mcs"
PPE_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-ppe"

PACKAGES += "kernel-module-ecm"
INSANE_SKIP_${PN} = "dev"

FRONT_END_NSS_ENABLE = "n"
FRONT_END_NSS_ENABLE_append_ipq40xx = "n"
FRONT_END_NSS_ENABLE_append_ipq807x-64 = "y"
FRONT_END_NSS_ENABLE_append_ipq807x = "y"

export ECM_FRONT_END_NSS_ENABLE="${FRONT_END_NSS_ENABLE}"

ECM_MAKE_OPTS_append += "ECM_IPV6_ENABLE=y "

ECM_MAKE_OPTS_ipq95xx_64 += "ECM_FRONT_END_SFE_ENABLE=y \
			ECM_FRONT_END_PPE_ENABLE=y \
			ECM_NON_PORTED_SUPPORT_ENABLE=y \
			ECM_INTERFACE_TUNIPIP6_ENABLE=y \
			ECM_INTERFACE_GRE_TUN_ENABLE=y \
			ECM_INTERFACE_GRE_TAP_ENABLE=y \
			ECM_INTERFACE_MAP_T_ENABLE=y \
			ECM_MULTICAST_ENABLE=y \
			ECM_INTERFACE_BOND_ENABLE=y \
			ECM_INTERFACE_VXLAN_ENABLE=y \
			ECM_INTERFACE_IPSEC_ENABLE=y \
			ECM_XFRM_ENABLE=y \
			"
ECM_MAKE_OPTS_ipq95xx += " ECM_FRONT_END_SFE_ENABLE=y \
			ECM_FRONT_END_PPE_ENABLE=y \
			ECM_NON_PORTED_SUPPORT_ENABLE=y \
			ECM_INTERFACE_TUNIPIP6_ENABLE=y \
			ECM_INTERFACE_GRE_TUN_ENABLE=y \
			ECM_INTERFACE_GRE_TAP_ENABLE=y \
			ECM_INTERFACE_MAP_T_ENABLE=y \
			ECM_MULTICAST_ENABLE=y \
			ECM_INTERFACE_BOND_ENABLE=y \
			ECM_INTERFACE_VXLAN_ENABLE=y \
			ECM_INTERFACE_IPSEC_ENABLE=y \
			ECM_XFRM_ENABLE=y \
			"

EXTRA_CFLAGS_ipq95xx_64 += "\
		-I${STAGING_INCDIR}/qca-nss-sfe \
		-I${STAGING_INCDIR}/nat46 \
		-I${STAGING_INCDIR}/qca-mcs \
		-I${STAGING_INCDIR}/qca-nss-ppe \
		"

EXTRA_CFLAGS_ipq95xx += "\
		-I${STAGING_INCDIR}/qca-nss-sfe \
		-I${STAGING_INCDIR}/nat46 \
		-I${STAGING_INCDIR}/qca-mcs \
		-I${STAGING_INCDIR}/qca-nss-ppe \
		"

#Using single qoutes to enacapsulate the path of Module.symvers
MODULE_EXTRA_SYMBOLS_ipq95xx_64 ="'${SFE_STG_INCDIR}/Module.symvers ${NAT46_STG_INCDIR}/Module.symvers "
MODULE_EXTRA_SYMBOLS_ipq95xx_64 += " ${MCS_STG_INCDIR}/Module.symvers ${PPE_STG_INCDIR}/Module.symvers' "

MODULE_EXTRA_SYMBOLS_ipq95xx ="'${SFE_STG_INCDIR}/Module.symvers ${NAT46_STG_INCDIR}/Module.symvers "
MODULE_EXTRA_SYMBOLS_ipq95xx += " ${MCS_STG_INCDIR}/Module.symvers ${PPE_STG_INCDIR}/Module.symvers' "

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH='${KARCH}' \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		KBUILD_EXTRA_SYMBOLS=${MODULE_EXTRA_SYMBOLS} \
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
	install -m 0644 exports/* ${D}${includedir}/qca-nss-ecm/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ecm/Module.symvers
}

SYSTEMD_SERVICE_${PN} += "qca-nss-ecm.service"

FILES_${PN} = " \
	${systemd_unitdir}/system/qca-nss-ecm.service \
	${bindir}/qca-nss-ecm \
	${bindir}/ecm_dump.sh \
	${sysconfdir}/sysctl.d/99-qca-nss-ecm.conf \
	"
